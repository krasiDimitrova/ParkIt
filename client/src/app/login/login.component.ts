import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { TokenStorageService } from '../services/token-storage.service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';

  form: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
  });

  constructor(private authService: AuthService, private tokenStorage: TokenStorageService, protected router: Router) {
  }

  ngOnInit(): void {
    if (this.tokenStorage.getToken()) {
      this.isLoggedIn = true;
    }
  }

  submit(): void {
    if (this.form.invalid) {
      return
    }
    const {email, password} = this.form.value;

    this.authService.login(email, password).subscribe(
      data => {
        this.tokenStorage.saveToken(data.headers.get("Authorization"));

        this.isLoginFailed = false;
        this.isLoggedIn = true;

        this.router.navigate(['']).then(() => {
          window.location.reload();
        });
      },
      err => {
        this.errorMessage = 'Invalid email or password';
        this.isLoginFailed = true;
      }
    );
  }
}
