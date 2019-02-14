from datetime import datetime
import os
from ctypes import windll
from tkinter import Tk, SUNKEN, messagebox, StringVar, OptionMenu
from tkinter.ttk import Label

import matplotlib
matplotlib.use('TkAgg')
import numpy as np
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.figure import Figure


class PyInterface:
    SHOW_NO_DATA_SENSORS = False

    def __init__(self):
        self.dataManager = DataManager()

        self.window = Tk()
        self.window.title("Laboratory monitor")

        self.sensorStatusesLabels = []
        self.sensorStatusesValues = dict()
        #self.graphOptionMenu = None

        sensorsForGraph = tuple(self.dataManager.getAllSensorNames(True))
        self.selection = StringVar(self.window)
        self.graphOptionMenuSelected = sensorsForGraph[0]
        self.selection.set(self.graphOptionMenuSelected)

        self.graphOptionMenu = OptionMenu(self.window, self.selection, *sensorsForGraph, command=self.updateGraph)
        self.graphOptionMenu.config(width=25)
        self.graphOptionMenu.grid(row=0, column=1, rowspan=2)

        self.canvas = None

        self.update()
        self.updateGraph()

        self.window.mainloop()

    def update(self, event=None):
        #### DELETE OLD
        for label in self.sensorStatusesLabels:
            label.destroy()

        self.sensorStatusesLabels.clear()

        #### LOAD CURRENT
        self.dataManager.getCurrentValues(self.sensorStatusesValues)

        ### CURRENT STATUSES
        for name in self.sensorStatusesValues:
            if self.SHOW_NO_DATA_SENSORS or self.sensorStatusesValues[name] != "NO DATA":
                if self.sensorStatusesValues[name] == "false":
                    status = name + ": OK"
                elif self.sensorStatusesValues[name] == "true":
                    status = name + ": NOT OK"
                else:
                    status = name + ": " + self.sensorStatusesValues[name]

                label = Label(text=status, font=("Arial", 14), relief=SUNKEN, width=35)
                label.grid(row=len(self.sensorStatusesLabels) + 2, column=0)

                self.sensorStatusesLabels.append(label)

        ### NOTIFICATION CHECK
        for notification in self.dataManager.getNotifications():
            messagebox.showwarning("Notification", notification)

        ### SCHEDULE ANOTHER REFRESH
        self.window.after(3000, self.update)

    def updateGraph(self, event=None):
        ### GRAPH DRAWING
        timestamps, values = self.dataManager.getGraphData(self.selection.get())
        timestampsMap = matplotlib.dates.date2num(timestamps)

        x = np.array(timestampsMap)
        y = np.array(values)

        fig = Figure(figsize=(20, 15))
        a = fig.add_subplot(111)
        a.plot(x, y, color='blue', linewidth=1)
        # a.invert_yaxis()

        a.set_title("Graph of " + self.selection.get(), fontsize=16)
        a.set_ylabel("Value", fontsize=14)
        a.set_xlabel("Time", fontsize=14)
        a.set_xlim([min(x) - 0.1, max(x) + 0.1])

        myFmt = matplotlib.dates.DateFormatter("%d.%m. %H:%M")
        fig.gca().xaxis.set_major_formatter(myFmt)

        if self.canvas is not None:
            self.canvas.get_tk_widget().destroy()

        self.canvas = FigureCanvasTkAgg(fig, master=self.window)
        self.canvas.get_tk_widget().grid(row=2, column=1, rowspan=len(self.sensorStatusesLabels))
        self.canvas.draw()


class DataManager:
    LOGS_PATH = "www" + os.sep + "data"
    DIRECTORY = os.fsencode(LOGS_PATH)

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
            open(self.LOGS_PATH + os.sep + "NOTIFICATIONS.txt", "w").close()    # delete content

        return result

    def getGraphData(self, sensor):
        timestamps = []
        values = []

        with open(self.LOGS_PATH + os.sep + sensor + ".csv", "r") as data:
            rows = data.read().splitlines()
            step = max(1, int(len(rows)/200))

            for row in rows[::step]:
                id, timestamp, value = row.split(";")

                timestamps.append(datetime.strptime(timestamp, '%Y-%m-%d %H:%M:%S'))

                try:
                    values.append(float(value))
                except ValueError:
                    if value == "false":
                        values.append(1)
                    elif value == "true":
                        values.append(0)

        return timestamps, values


windll.shcore.SetProcessDpiAwareness(1)
gui = PyInterface()