import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from './services/token-storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  isLoggedIn = false;
  router: Router;

  constructor(private tokenStorageService: TokenStorageService, router: Router) {
    this.router = router;
  }

  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();

    if (!this.isLoggedIn) {
      this.redirectToLogin();
    }
  }

  logout(): void {
    this.tokenStorageService.signOut();
    window.location.reload();
  }

  private redirectToLogin() {
    this.router.navigate(['/login']);
  }
}
