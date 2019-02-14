import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';


@Injectable()
export class DataService {
  //private apiUrl: string = 'http://147.213.232.125/'; //TODO: tu bude SAV adresa kde je ulozeny subor
  //private apiUrl: string = 'http://147.213.232.125/data/';
  private apiUrl: string = 'http://localhost:4200/'

  private httpOptions = {
    //headers: new HttpHeaders({}),
    responseType: 'text'
  };
  
  constructor(private http: HttpClient) { }
  public get = (url: string, params?: HttpParams): Observable<any> => {
    //console.log(this.apiUrl + url);
    //this.httpOptions.headers.append("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    return this.http.get(this.apiUrl + url + '.csv', {responseType: 'text'}).pipe(catchError(this.handleError));
  };


  private handleError(error) {
    return (throwError(error || 'Server error'));
    //return error;
  }

}

//http://147.213.232.125/home/piestany/urychlovac/logs/DHT22_temperature_a1_1.csv