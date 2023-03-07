import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from './config.service';


const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl: string;

  constructor(private http: HttpClient, private configService: ConfigService) {
    this.baseUrl = configService.config.apiUrl;
  }

  login(email: string, password: string): Observable<HttpResponse<any>> {
    return this.http.get(`${this.baseUrl}/login?email=${email}&password=${password}`, {observe: 'response' });
  }

  register(firstName: string, lastName: string, email: string, password: string): Observable<any> {
    return this.http.post(this.baseUrl + '/register', {
      firstName,
      lastName,
      email,
      password
    }, httpOptions);
  }
}
