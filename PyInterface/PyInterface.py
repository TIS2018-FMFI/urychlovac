import sys
from datetime import datetime, timedelta
import os
from ctypes import windll
from enum import Enum
from tkinter import Tk, SUNKEN, messagebox, StringVar, OptionMenu, RAISED, RIDGE, GROOVE, CENTER
from tkinter.ttk import Label
import matplotlib
import numpy as np
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.figure import Figure

matplotlib.use('TkAgg')


class PyInterface:
    SHOW_NO_DATA_SENSORS = False
    GRAPH_WIDTH = 12
    GRAPH_HEIGHT = 8

    def __init__(self):
        self.dataManager = DataManager()

        self.window = Tk()
        self.window.title("Laboratory monitor")

        # current statuses labels
        self.sensorStatusesLabels = []
        self.sensorStatusesValueLabels = []
        self.sensorStatusesValues = dict()

        # current statuses header label
        self.headerLabel = Label(text="Current statuses", font=("Arial", 14, 'bold'),
                                 relief=GROOVE, width=38, anchor=CENTER)
        self.headerLabel.grid(row=0, column=0, rowspan=2, columnspan=2)

        # graph sensor selection
        sensorsForGraph = tuple(self.dataManager.getAllSensorNames(True))
        self.graphMenuSelection = StringVar(self.window)
        self.graphMenuSelection.set(sensorsForGraph[0])

        self.graphOptionMenu = OptionMenu(self.window, self.graphMenuSelection, *sensorsForGraph,
                                          command=self.updateGraph)
        self.graphOptionMenu.config(width=30)
        self.graphOptionMenu.grid(row=0, column=2, rowspan=2)

        # graph period selection
        self.daysLabels = dict()
        self.daysLabels["Show last 24 hours"] = 1
        self.daysLabels["Show last 48 hours"] = 2
        self.daysLabels["Show last 72 hours"] = 3
        self.daysLabels["Show last 4 days"] = 4
        self.daysLabels["Show last 5 days"] = 5
        self.daysLabels["Show all data"] = 32000

        daysForGraph = tuple(self.daysLabels.keys())
        self.graphTimeMenuSelection = StringVar(self.window)
        self.graphTimeMenuSelection.set(daysForGraph[-1])

        self.graphTimeOptionMenu = OptionMenu(self.window, self.graphTimeMenuSelection, *daysForGraph,
                                              command=self.updateGraph)
        self.graphTimeOptionMenu.config(width=20)
        self.graphTimeOptionMenu.grid(row=0, column=3, rowspan=2)

        # graph resolution selection
        self.resolutionLabels = dict()
        self.resolutionLabels["VERY LOW RESOLUTION (50 values)"] = 50
        self.resolutionLabels["LOW RESOLUTION (100 values)"] = 100
        self.resolutionLabels["MEDIUM RESOLUTION (200 values)"] = 200
        self.resolutionLabels["HIGH RESOLUTION (300 values)"] = 300
        self.resolutionLabels["VERY HIGH RESOLUTION (400 values)"] = 400

        resolutionsForGraph = tuple(self.resolutionLabels.keys())
        self.graphResMenuSelection = StringVar(self.window)
        self.graphResMenuSelection.set(resolutionsForGraph[2])

        self.graphResOptionMenu = OptionMenu(self.window, self.graphResMenuSelection, *resolutionsForGraph,
                                              command=self.updateGraph)
        self.graphResOptionMenu.config(width=30)
        self.graphResOptionMenu.grid(row=0, column=4, rowspan=2)

        # graph mode selection
        self.modeLabels = dict()
        self.modeLabels["Every n-th value (faster)"] = 0
        self.modeLabels["Average value (nicer)"] = 1

        modesForGraph = tuple(self.modeLabels.keys())
        self.graphModeMenuSelection = StringVar(self.window)
        self.graphModeMenuSelection.set(modesForGraph[0])

        self.graphModeOptionMenu = OptionMenu(self.window, self.graphModeMenuSelection, *modesForGraph,
                                             command=self.updateGraph)
        self.graphModeOptionMenu.config(width=20)
        self.graphModeOptionMenu.grid(row=0, column=5, rowspan=2)

        # misc setup
        self.canvas = None

        self.update()
        self.updateGraph()

        self.window.mainloop()

    def update(self, event=None):
        #### DELETE OLD
        for label in self.sensorStatusesLabels:
            label.destroy()

        self.sensorStatusesLabels.clear()

        for label in self.sensorStatusesValueLabels:
            label.destroy()

        self.sensorStatusesValueLabels.clear()

        #### LOAD CURRENT
        self.dataManager.getCurrentValues(self.sensorStatusesValues)

        ### CURRENT STATUSES
        for name in self.sensorStatusesValues:
            if self.SHOW_NO_DATA_SENSORS or self.sensorStatusesValues[name] != "NO DATA":
                if self.sensorStatusesValues[name] == "false":
                    status = "OK"
                elif self.sensorStatusesValues[name] == "true":
                    status = "NOT OK"
                elif "temperature" in name:
                    status = self.sensorStatusesValues[name] + " °C"
                elif "humidity" in name:
                    status = self.sensorStatusesValues[name] + "%"
                else:
                    status = self.sensorStatusesValues[name]

                label = Label(text=name, font=("Arial", 14), relief=GROOVE, width=30)
                label.grid(row=len(self.sensorStatusesLabels) + 2, column=0)

                self.sensorStatusesLabels.append(label)

                label = Label(text=status, font=("Arial", 14, 'bold'), relief=SUNKEN, width=8)
                label.grid(row=len(self.sensorStatusesValueLabels) + 2, column=1)

                self.sensorStatusesValueLabels.append(label)

        ### NOTIFICATION CHECK
        for notification in self.dataManager.getNotifications():
            messagebox.showwarning("Notification", notification)

        ### SCHEDULE ANOTHER REFRESH
        self.window.after(3000, self.update)

    def updateGraph(self, event=None):
        ### GRAPH DRAWING
        timestamps, values = self.dataManager.getGraphData(self.graphMenuSelection.get(),
                                                           self.daysLabels[self.graphTimeMenuSelection.get()],
                                                           self.resolutionLabels[self.graphResMenuSelection.get()],
                                                           self.modeLabels[self.graphModeMenuSelection.get()])
        timestampsMap = matplotlib.dates.date2num(timestamps)

        x = np.array(timestampsMap)
        y = np.array(values)

        fig = Figure(figsize=(PyInterface.GRAPH_WIDTH, PyInterface.GRAPH_HEIGHT))
        a = fig.add_subplot(111)
        a.plot(x, y, color='blue', linewidth=1)

        a.set_title("Graph of " + self.graphMenuSelection.get(), fontsize=16)
        a.set_ylabel("Value", fontsize=14)
        a.set_xlabel("Time", fontsize=14)
        a.set_xlim([min(x) - 0.1, max(x) + 0.1])

        myFmt = matplotlib.dates.DateFormatter("%d.%m. %H:%M")
        fig.gca().xaxis.set_major_formatter(myFmt)

        if self.canvas is not None:
            oldGraph = self.canvas.get_tk_widget()
        else:
            oldGraph = None

        self.canvas = FigureCanvasTkAgg(fig, master=self.window)
        self.canvas.get_tk_widget().grid(row=2, column=2, rowspan=len(self.sensorStatusesLabels), columnspan=4)
        self.canvas.draw()

        if oldGraph is not None:
            oldGraph.destroy()


class DataManager:
    LOGS_PATH = "www" + os.sep + "data"
    DIRECTORY = os.fsencode(LOGS_PATH)
    FLOAT = 1
    BOOL = 2

    def getAllSensorNames(self, mustContainData=False):
        result = []

        if (mustContainData == False):
            for file in os.listdir(self.DIRECTORY):
                filename = os.fsdecode(file)

                if filename == "NOTIFICATIONS.txt":
                    continue

                if filename.endswith(".csv"):
                    filename = os.fsdecode(file)
                    result.append(filename[:-4])
        else:
            for file in os.listdir(self.DIRECTORY):
                filename = os.fsdecode(file)

                if filename == "NOTIFICATIONS.txt":
                    continue

                with open(self.LOGS_PATH + os.sep + filename, "r") as log:
                    lines = log.read().splitlines()

                    if len(lines) > 0:
                        result.append(filename[:-4])

        return result

    def getCurrentValues(self, valueDict):
        for file in os.listdir(self.DIRECTORY):
            filename = os.fsdecode(file)

            if filename == "NOTIFICATIONS.txt":
                continue

            with open(self.LOGS_PATH + os.sep + filename, "r") as log:
                lines = log.read().splitlines()

                if len(lines) == 0:
                    valueDict[filename[:-4]] = "NO DATA"
                else:
                    current = lines[-1]
                    valueDict[filename[:-4]] = current.split(";")[-1]

    def getNotifications(self):
        with open(self.LOGS_PATH + os.sep + "NOTIFICATIONS.txt", "r") as notificationFile:
            result = notificationFile.read().splitlines()

        if len(result) > 0:
            open(self.LOGS_PATH + os.sep + "NOTIFICATIONS.txt", "w").close()  # delete content

        return result

    def getGraphData(self, sensor, timePeriod, resolution, mode):
        timestamps = []
        values = []

        with open(self.LOGS_PATH + os.sep + sensor + ".csv", "r") as data:
            rows = data.read().splitlines()
            start = self.findDateIndex(timePeriod, rows)

            if self.getGraphType(rows[start]) == self.FLOAT:
                if mode == 0:
                    step = max(1, int((len(rows) - start) / resolution))

                    for row in rows[start::step]:
                        id, timestamp, value = row.split(";")
                        timestamps.append(datetime.strptime(timestamp, '%Y-%m-%d %H:%M:%S'))
                        values.append(float(value))

                elif mode == 1:
                    chunkSize = max(1, int((len(rows) - start) / resolution))
                    for chunk in self.chunks(rows, chunkSize):
                        chunkTimestamps = []
                        chunkValues = []

                        for row in chunk:
                            id, timestamp, value = row.split(";")
                            chunkTimestamps.append(datetime.strptime(timestamp, '%Y-%m-%d %H:%M:%S').timestamp())
                            chunkValues.append(float(value))

                        timestamps.append(datetime.fromtimestamp(sum(chunkTimestamps)/float(len(chunkTimestamps))))
                        values.append(sum(chunkValues)/float(len(chunkValues)))     # float to get float

            elif self.getGraphType(rows[start]) == self.BOOL:
                # add first value
                id, timestamp, value = rows[start].split(";")

                timestamps.append(datetime.strptime(timestamp, '%Y-%m-%d %H:%M:%S'))
                values.append(self.strToInt(value))

                lastRow = rows[start]

                # add every change
                for row in rows[start + 1::]:
                    id, timestamp, value = row.split(";")

                    if self.strToInt(value) != values[-1]:
                        lastId, lastTimestamp, lastValue = lastRow.split(";")

                        timestamps.append(datetime.strptime(lastTimestamp, '%Y-%m-%d %H:%M:%S'))
                        values.append(self.strToInt(lastValue))

                        timestamps.append(datetime.strptime(timestamp, '%Y-%m-%d %H:%M:%S'))
                        values.append(self.strToInt(value))

                    lastRow = row

                # add last value, if it's not already contained
                id, timestamp, value = rows[-1].split(";")

                if timestamps[-1] != timestamp:
                    timestamps.append(datetime.strptime(timestamp, '%Y-%m-%d %H:%M:%S'))
                    values.append(self.strToInt(value))

        return timestamps, values

    def findDateIndex(self, numOfDays, rows):
        start = 0
        end = len(rows) - 1
        threshold = datetime.now() - timedelta(days=numOfDays)

        while start <= end:
            mid = (start + end) // 2
            lookUpValue = datetime.strptime(rows[mid].split(';')[1], '%Y-%m-%d %H:%M:%S')

            if lookUpValue < threshold:
                start = mid + 1
            elif lookUpValue > threshold:
                end = mid - 1
            else:
                return mid

        return mid

    def getGraphType(self, row):
        id, timestamp, value = row.split(";")

        if value == "true" or value == "false":
            return self.BOOL

        return self.FLOAT

    def strToInt(self, string):
        if string == "false":
            return 0
        elif string == "true":
            return 1

    def chunks(self, list, size):
        """Yield successive size sized chunks from list."""
        # source https://stackoverflow.com/a/312464/9682679
        for i in range(0, len(list), size):
            yield list[i:i + size]


windll.shcore.SetProcessDpiAwareness(1)
gui = PyInterface()
