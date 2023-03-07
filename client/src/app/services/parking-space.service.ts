import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ConfigService } from './config.service';
import { Observable } from 'rxjs';
import { ParkingSpace } from './parking-space';

@Injectable({
  providedIn: 'root'
})
export class ParkingSpaceService {

  private httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json'})
  };

  private baseUrl: string;

  private radius = 1000;

  constructor(private http: HttpClient, private configService: ConfigService) {
    this.baseUrl = configService.config.apiUrl;
  }

  findSpace(latitude: number, longitude: number): Observable<ParkingSpace> {
    return this.http.get<ParkingSpace>(`${this.baseUrl}/api/v1/parking-space?latitude=${latitude}` +
      `&longitude=${longitude}&radius=${this.radius}`, this.httpOptions);
  }


  getSpace(id: number): Observable<ParkingSpace> {
    return this.http.get<ParkingSpace>(`${this.baseUrl}/api/v1/parking-space/${id}`, this.httpOptions);
  }

  reserveSpace(id: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/api/v1/parking-space/${id}/reserve`, this.httpOptions);
  }
}
