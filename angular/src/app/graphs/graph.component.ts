import { Component, OnInit, Input, OnChanges } from '@angular/core';
import * as _ from 'lodash';
import * as Highcharts from "highcharts";
import { PeriodListHours, PeriodListDays, PeriodListMinutes } from '../shared/period.enum';
import { TypeTemperatureList, TypeHumidityList } from '../shared/type.enum';
import { GraphDataService } from '../service/graph-data.service';
window['Highcharts'] = Highcharts;

@Component({
  selector: 'graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graphs.component.css']
})
export class GraphComponent {
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

  constructor(private graphDataService: GraphDataService){
    this.graphDataService.getGraphData('').subscribe(data => 
      { console.log(data);
        //data.forEach(element => {
          //this.graphData.push([element.date.getTime(), element.value])
        //});
        //this.graphDataFull = _.cloneDeep(this.graphData);
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
    console.log(this.typeLists[this.graphType][this.graphNameIndex]);
    // this.chart.title.text = this.graphName;
    // this.graphDataService.getGraphData(this.graphType + '/' + this.graphName + '/' + this.periodList[this.periodIndex].beValue).subscribe(
    //   data => this.graphData.push(data)
    // );
  }

  filterDataByDate(date: Date, now: Date){
    //console.log(date, now);
    let novy = now;
    novy.setDate(now.getDate() - 1);
    this.graphData = [[novy.getTime(), 19.2]];
    this.graphDataFull.forEach((el) =>{
      // console.log(el[0], now.getTime(), date.getTime());
      if(_.inRange(el[0], date.getTime(), now.getTime())) {
        this.graphData.push(el);
        console.log(el[0], date.getTime(), now.getTime());
      }
    });  
    //console.log(this.graphData);
  }
}


