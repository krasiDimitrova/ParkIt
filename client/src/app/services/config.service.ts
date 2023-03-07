import { Injectable } from '@angular/core';
import { HttpBackend, HttpClient } from '@angular/common/http';

export interface Config {
  apiUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private readonly LOCAL_API_URL = '/server';

  private readonly API_URL = '';

  private appConfig: Config;

  constructor(private http: HttpClient, handler: HttpBackend) {
    this.http = new HttpClient(handler);
    this.appConfig = {"apiUrl": ""}
  }

  get config(): Config {
    return this.appConfig;
  }

  public load(): void {
    const location = window.location.origin;
    this.appConfig = {"apiUrl": this.LOCAL_API_URL};
  }
}
