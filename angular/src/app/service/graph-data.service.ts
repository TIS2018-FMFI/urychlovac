import { Injectable } from '@angular/core';
import { DataService } from './data.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
@Injectable()
export class GraphDataService {

  constructor(private dataService: DataService) { }

  public getGraphData(type: string): Observable<any> {
    const url = type;
    return this.dataService.get(url).pipe(
      map(graphData => graphData)
      ); //TODO: subscribe
  }
}
