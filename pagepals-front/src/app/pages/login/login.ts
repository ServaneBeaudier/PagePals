import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { TokenStorage } from '../../core/token-storage';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [RouterLink, RouterModule, CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  loginForm;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private tokenStorage: TokenStorage
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }


  onSubmit() {
  this.errorMessage = null;

  if (this.loginForm.valid) {
    const email = this.loginForm.value.email!;
    const motDePasse = this.loginForm.value.password!;

    this.authService.login({ email, motDePasse }).subscribe({
      next: (response) => {
        console.log('Réponse login :', response);
        this.tokenStorage.saveToken(response.token);
        localStorage.setItem('userId', response.id.toString());
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Mail ou mot de passe incorrect.';
      }
    });
  }
}


}
