import { Component, Input, OnChanges } from '@angular/core';
import * as _ from 'lodash';
import { SCATTER_CHART } from '../charts';

@Component({
  selector: 'scatter-chart',
  template: `<div [ng2-highcharts]="chart"></div>`
})
export class ScatterChartComponent implements OnChanges {
  @Input() data;
  @Input() graphType;
  @Input() period;
  @Input() yAxisTitle = '';
  chart;


  ngOnChanges() {
    const clonedChart = _.cloneDeep(SCATTER_CHART);
    clonedChart.series[0].data = this.data;
    setTimeout(() => {
      
      this.chart = {
        ...clonedChart
      };
      this.chart.yAxis.title.text = this.yAxisTitle;
    })
  }
}
