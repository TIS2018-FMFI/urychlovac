import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';


@Injectable()
export class DataService {
  //private apiUrl: string = 'http://localhost:8080/'; //TODO: tu bude SAV adresa kde je ulozeny subor
  private apiUrl: string = 'http://147.213.232.125/home/piestany/urychlovac/logs/';

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };
  
  constructor(private http: HttpClient) { }
  public get = (url: string, params?: HttpParams): Observable<any> => {
    console.log(this.apiUrl + url);
    this.httpOptions.headers.append("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    return this.http.get(this.apiUrl + url, this.httpOptions).pipe(catchError(this.handleError));
  };


  private handleError(error) {
    // return Observable.pipe(throwError(error || 'Server error'));
    return [];
  }

}