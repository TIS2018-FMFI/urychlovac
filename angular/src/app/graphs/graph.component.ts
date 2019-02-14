import { Component, OnInit, Input, OnChanges, ApplicationRef, ChangeDetectorRef } from '@angular/core';
import * as _ from 'lodash';
import * as Highcharts from "highcharts";
import { PeriodListHours, PeriodListDays, PeriodListMinutes } from '../shared/period.enum';
import { TypeTemperatureList, TypeHumidityList } from '../shared/type.enum';
import { GraphDataService } from '../service/graph-data.service';
import { LabData } from '../shared/labData.model';
import * as converter from 'json-2-csv';
window['Highcharts'] = Highcharts;

@Component({
  selector: 'graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graphs.component.css']
})
export class GraphComponent implements OnInit{
  @Input() graphType;
  @Input() yAxisTitle = '';
  periodIndex = 0;
  periodList = [...PeriodListHours, ...PeriodListDays, ...PeriodListMinutes];
  typeLists = {
    'temperature': TypeTemperatureList,
    'humidity': TypeHumidityList 
  };
  graphDataFull = [];
  graphData = [];
  graphNameIndex: number = 0;
  graphName: string;
  header: string = 'id;date;value\n';
  options = {
    delimiter: {
      field: ';'
    },
  };


  constructor(private graphDataService: GraphDataService, private ref: ApplicationRef){}

  ngOnInit(){
    //console.log("nazov: ", this.typeLists[this.graphType][this.graphNameIndex].name);
    this.graphDataService.getGraphData(this.typeLists[this.graphType][this.graphNameIndex].name)
    .subscribe(data => 
      { //console.log("data: ", data);
        this.convertCsvStringToJson(data).then(data => {
          data.forEach(element => {
            this.graphData.push([element.date.getTime(), element.value])
          });
          this.graphDataFull = _.cloneDeep(this.graphData);
          this.changeInterval();
        })
      } 
    );
  }

  changeInterval(){
    //console.log("period: ", this.periodList[this.periodIndex]);
    const period = this.periodList[this.periodIndex].beValue.split('_');
    const now = new Date();
    now.setHours(now.getHours() + 1);

    let lastDateToLookFor = new Date();
    lastDateToLookFor.setHours(lastDateToLookFor.getHours() + 1);
    
    switch(period[1]) {
      case 'DAY':
        lastDateToLookFor.setDate(now.getDate() - Number(period[0]));
        break;
      case 'HOUR':
        lastDateToLookFor.setHours(now.getHours() - Number(period[0]));
        break;
      case 'MIN':
        lastDateToLookFor.setMinutes(now.getMinutes() - Number(period[0]));
        break;
    }
    //console.log(lastDateToLookFor, now);
    this.filterDataByDate(lastDateToLookFor, now);
  }

  changeGraphType(){//TODO: -  bude volat metodu v service
    // console.log(this.typeLists[this.graphType][this.graphNameIndex]);
    this.graphData = [];
    this.graphDataService.getGraphData(this.typeLists[this.graphType][this.graphNameIndex].name)
    .subscribe(data => 
      { 
        this.convertCsvStringToJson(data).then(data => {
          data.forEach(element => {
            this.graphData.push([element.date.getTime(), element.value])
            //console.log("element: ", element);
          });
          this.graphDataFull = _.cloneDeep(this.graphData);
        })
      }
    );this.ref.tick();
  }

  filterDataByDate(date: Date, now: Date){
    //console.log(date, now);
    let novy = now;
    novy.setDate(now.getDate());
    this.graphData = [];
    //console.log("now: ", now.getDate());
    this.graphDataFull.forEach((el) =>{
       //console.log("filter podla casu: ", el[0], now.getTime(), date.getTime());
       //console.log(el);
      if(el[0] < now.getTime() && el[0] > date.getTime()) {
        this.graphData.push(el);
        //console.log("Presiel: ", el[0], date.getTime(), now.getTime());
      }
    });
    //console.log(this.graphData);
  }

  convertCsvStringToJson(csv: string) {
    // csv = this.header + "Nissan;Murano;2013\n" +
    //       "BMW;X5;2014\n";
    csv = this.header + csv;

    return converter.csv2jsonAsync(csv, this.options).then(
      (jsonObjs) => {
        let helpArray = Array<LabData>();
        jsonObjs.forEach(element => {
         let dateTimeParts = element.date.split(' ');
          let timeParts = dateTimeParts[1].split(':');
          let dateParts = dateTimeParts[0].split('.');
          //console.log("date: ", timeParts);
          let el = (new LabData(element.id, new Date(
                                                          parseInt(dateParts[2]), 
                                                          parseInt(dateParts[1], 10) - 1, 
                                                          parseInt(dateParts[0]), 
                                                          parseInt(timeParts[0]) + 1, 
                                                          parseInt(timeParts[1]), 
                                                          parseInt(timeParts[2]) || 0
                                                          ),
                                                      element.value))
          helpArray.push(el);
          }
        );
        return helpArray
      }
    );
  }
}


