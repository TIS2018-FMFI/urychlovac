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
  graphName: number = 0;

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
    this.chart.title.text = this.typeLists[this.graphType][this.graphName].name;
    this.chart.yAxis.title.text = this.yAxisTitle;

    this.graphDataService.getGraphData(this.graphType + '/Acc/3hod').subscribe(
      data => console.log(data)
    );
  }

  changeInterval(){
    //TODO: interval na grafy -  bude volat metodu v service
    console.log(this.periodList[this.periodIndex]);
  }

  changeGraphType(){//TODO:
    console.log(this.typeLists[this.graphType][this.graphName]);
  }
  
}
