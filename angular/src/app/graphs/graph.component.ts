import { Component, OnInit, Input } from '@angular/core';
import { LINE_CHART, SCATTER_CHART } from '../shared/charts';
import * as _ from 'lodash';
import * as Highcharts from "highcharts";
import { PeriodListHours, PeriodListDays, PeriodListMinutes } from '../shared/period.enum';
import { TypeTemperatureList, TypeHumidityList } from '../shared/type.enum';
import { GraphDataService } from '../service/graph-data.service';
window['Highcharts'] = Highcharts;

@Component({
  selector: 'app-line-graph',
  templateUrl: './graph.component.html',
  styleUrls: ['./graphs.component.css']
})
export class GraphComponent implements OnInit{
  @Input() graphType;
  @Input() yAxisTitle = '';
  chart;
  periodIndex = 0;
  periodList = [...PeriodListHours, ...PeriodListDays, ...PeriodListMinutes];
  typeLists = {
    'temperature': TypeTemperatureList,
    'humidity': TypeHumidityList 
  };
  graphData = [];
  graphNameIndex: number = 0;
  graphName: string;

  constructor(private graphDataService: GraphDataService){

    const clonedChart = _.cloneDeep(SCATTER_CHART);

    const chart = {
      ...clonedChart
    };

    this.chart = chart;
    //this.chart.title.text = this.typeLists[this.graphType][this.graphName].name;
    // console.log(JSON.stringify(this.chart));
  }

  ngOnInit(){
    this.graphName = this.typeLists[this.graphType][this.graphNameIndex].name;
    this.chart.title.text = this.graphName;
    this.chart.yAxis.title.text = this.yAxisTitle;

    this.graphDataService.getGraphData(this.graphType + '/' + this.graphName + '/' + this.periodList[this.periodIndex].beValue).subscribe(
      data => this.graphData.push(data)
    );
  }

  changeInterval(){
    //TODO: interval na grafy -  bude volat metodu v service
    console.log(this.periodList[this.periodIndex]);
    this.graphDataService.getGraphData(this.graphType + '/' + this.graphName + '/' + this.periodList[this.periodIndex].beValue).subscribe(
      data => this.graphData.push(data)
    );
  }

  changeGraphType(){//TODO:
    console.log(this.typeLists[this.graphType][this.graphNameIndex]);
    this.chart.title.text = this.graphName;
    this.graphDataService.getGraphData(this.graphType + '/' + this.graphName + '/' + this.periodList[this.periodIndex].beValue).subscribe(
      data => this.graphData.push(data)
    );
  }
  
}
