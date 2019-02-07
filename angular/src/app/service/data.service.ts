import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';


@Injectable()
export class DataService {
  private apiUrl: string = 'http://localhost:8080/';

  constructor(private http: HttpClient) { }
  public get = (url: string, params?: HttpParams): Observable<any> => {
    // console.log(this.apiUrl + url);
    return this.http.get(this.apiUrl + url, { params }).pipe(catchError(this.handleError));
  };


  private handleError(error) {
    // return Observable.pipe(throwError(error || 'Server error'));
    return [];
  }

}