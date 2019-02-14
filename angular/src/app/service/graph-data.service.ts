import { Injectable } from '@angular/core';
import { DataService } from './data.service';

import { map } from 'rxjs/internal/operators/map';
import { LabData } from '../shared/labData.model';
import * as converter from 'json-2-csv';
@Injectable()
export class GraphDataService {

  header: string = 'id;date;value\n';
  options = {
    delimiter: {
      field: ';'
    },
  };

  constructor(private dataService: DataService) {}

  public getGraphData(type: string) {
    const fileName = type;
    //console.log(this.fakeData);
    //return this.convertCsvStringToJson(this.fakeData);
    return this.dataService.get(fileName)//.subscribe(data =>
    //      console.log(data)
    // );
      //map(graphData => graphData)//new LabData(graphData.id, graphData.value, graphData.date))
      //); 
  }
}
