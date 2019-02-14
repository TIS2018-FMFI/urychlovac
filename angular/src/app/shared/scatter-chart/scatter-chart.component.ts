import { Component, Input, OnChanges } from '@angular/core';
import * as _ from 'lodash';
import { SCATTER_CHART } from '../charts';
import { Period } from '../period.enum';

@Component({
  selector: 'scatter-chart',
  template: `<div [ng2-highcharts]="chart"></div>`
})
export class ScatterChartComponent implements OnChanges {
  @Input() data: any;
  @Input() graphType;
  @Input() period;
  @Input() yAxisTitle = '';
  chart;


  ngOnChanges() {
    const clonedChart = _.cloneDeep(SCATTER_CHART);

    setTimeout(() => {
      clonedChart.series[0].data = this.data;
      this.chart = {
        ...clonedChart
      };
      this.chart.yAxis.title.text = this.yAxisTitle;
    })
  }
}
