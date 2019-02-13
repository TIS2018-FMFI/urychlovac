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

  fakeData=
  '0;12-2-2019 00:50:28;19.6\n'+
  '0;12-2-2019 00:50:29;19.6\n'+
  '0;12-2-2019 00:50:30;19.6\n'+
  '0;12-2-2019 00:50:31;19.5\n'+
  '0;12-2-2019 00:50:32;19.5\n'+
  '0;12-2-2019 00:50:33;19.5\n'+
  '0;12-2-2019 00:50:34;19.6\n'+
  '0;12-2-2019 00:50:35;19.6\n'+
  '0;12-2-2019 00:50:36;19.6\n'+
  '0;12-2-2019 00:50:37;19.6\n'+
  '0;12-2-2019 00:50:38;19.6\n'+
  '0;12-2-2019 00:50:39;19.6\n'+
  '0;12-2-2019 00:50:40;19.6\n'+
  '0;12-2-2019 00:50:41;19.6\n'+
  '0;12-2-2019 00:50:42;19.6\n'+
  '0;12-2-2019 00:50:43;19.6\n'+
  '0;12-2-2019 00:50:44;19.6\n'+
  '0;12-2-2019 00:50:45;19.6\n'+
  '0;12-2-2019 00:50:46;19.6\n'+
  '0;12-2-2019 00:50:47;19.6\n'+
  '0;12-2-2019 00:50:48;19.6\n'+
  '0;12-2-2019 00:50:49;19.6\n'+
  '0;12-2-2019 00:50:50;19.6\n'+
  '0;12-2-2019 00:50:51;19.6\n'+
  '0;12-2-2019 00:50:52;19.6\n'+
  '0;12-2-2019 00:50:53;19.6'

  constructor(private dataService: DataService) {}

  public getGraphData(type: string) {
    const fileName = type;
    //return this.convertCsvStringToJson(this.fakeData);
    return this.dataService.get('DH22_temperature_a1_1');
      //map(graphData => graphData)//new LabData(graphData.id, graphData.value, graphData.date))
      //); 

  }

  convertCsvStringToJson(csv: string) {
    // csv = this.header + "Nissan;Murano;2013\n" +
    //       "BMW;X5;2014\n";
    csv = this.header + this.fakeData;

    return converter.csv2jsonAsync(csv, this.options).then(
      (jsonObjs) => {
        let helpArray = Array<LabData>();
        jsonObjs.forEach(element => {
          let dateTimeParts = element.date.split(' ');
          let timeParts = dateTimeParts[1].split(':');
          let dateParts = dateTimeParts[0].split('-');
          helpArray.push(new LabData(element.id, new Date(
                                                          dateParts[2], 
                                                          parseInt(dateParts[1], 10) - 1, 
                                                          dateParts[0], 
                                                          timeParts[0] + 1, 
                                                          timeParts[1], 
                                                          timeParts[2]
                                                          ),
                                                          element.value))
        });
        return helpArray
      }
    );
  }
}
