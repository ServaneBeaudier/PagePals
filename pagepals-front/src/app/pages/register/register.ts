import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { TokenStorage } from '../../core/token-storage';
import { CommonModule } from '@angular/common';
import { Confidentialite } from '../confidentialite/confidentialite';

@Component({
  selector: 'app-register',
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule, Confidentialite],
})
export class Register {
  registerForm;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenStorage: TokenStorage,
    private router: Router
  ) {
    this.registerForm = this.fb.group(
      {
        pseudo: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required],
        acceptPolicy: [false, Validators.requiredTrue],
      },
      {
        validators: this.passwordsMatchValidator,
      }
    );
  }

  passwordsMatchValidator(form: AbstractControl) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    if (password !== confirmPassword) {
      return { passwordsMismatch: true };
    }
    return null;
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const { pseudo, email, password } = this.registerForm.value;

      this.authService.register({
        pseudo: pseudo!,
        email: email!,
        motDePasse: password!,
      }).subscribe({
        next: (response) => {
          console.log('Inscription réussie', response);
          this.router.navigate(['/profile']);
        },
        error: (err) => {
          console.error('Erreur lors de l\'inscription', err);
        },
      });
    } else {
      console.log('Formulaire invalide');
    }
  }

  isPolicyModalOpen = false;
  openPolicy() {
    this.isPolicyModalOpen = true;
  }
  closePolicy() {
    this.isPolicyModalOpen = false;
  }

  onModalClick(event: MouseEvent) {
    event.stopPropagation();
  }
}
