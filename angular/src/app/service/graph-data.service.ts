import { Injectable } from '@angular/core';
import { DataService } from './data.service';
@Injectable()
export class GraphDataService {

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
