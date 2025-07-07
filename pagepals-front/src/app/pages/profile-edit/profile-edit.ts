import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService, UpdateMailDTO, UpdatePasswordDTO } from '../../core/auth.service';
import { UpdateUserProfileDTO, UserService } from '../../core/user.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-profile-edit',
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './profile-edit.html',
  styleUrl: './profile-edit.css'
})
export class ProfileEdit implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  userId: number | null = null;

  profileForm = this.fb.group({
    pseudo: ['', Validators.required],
    bio: [''],
    photoProfil: [''],
    mail: [''],
    oldPassword: [''],
    newPassword: [''],
    confirmPassword: [''],
  });

  photoFile?: File;

  showDeleteConfirm = false; // contrôle affichage modale suppression

  ngOnInit() {
    this.userId = this.authService.getUserIdFromStorage();
    if (!this.userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.userService.getUserProfile(this.userId).subscribe(profile => {
      console.log('Profil reçu:', profile);
      this.profileForm.patchValue({
        pseudo: profile.pseudo ?? '',
        bio: profile.bio ?? '',
        photoProfil: profile.photoProfil ?? '',
      });
    });

    this.authService.getEmail(this.userId).subscribe(response => {
      this.profileForm.patchValue({ mail: response.email ?? '' });
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.photoFile = input.files[0];
    }
  }

  getPhotoUrl(fileName: string | null | undefined): string {
    if (fileName) {
      return `/api/user/photo/${encodeURIComponent(fileName)}`;
    }
    return 'assets/images/icons8/photo-profil.png';
  }

  onSubmit() {
    if (this.profileForm.invalid || this.userId === null) return;

    const { pseudo, bio, mail, oldPassword, newPassword, confirmPassword } = this.profileForm.value;

    const isPasswordFilled = oldPassword || newPassword || confirmPassword;
    if (isPasswordFilled) {
      if (!oldPassword || !newPassword || !confirmPassword) {
        alert('Pour changer de mot de passe, veuillez remplir tous les champs du mot de passe.');
        return;
      }
      if (newPassword !== confirmPassword) {
        alert('Le nouveau mot de passe et sa confirmation ne correspondent pas.');
        return;
      }
    }

    const updateDto: UpdateUserProfileDTO = {
      id: this.userId,
      pseudo: pseudo ?? undefined,
      bio: bio ?? undefined,
    };

    this.userService.updateUserProfile(updateDto).subscribe(() => {
      if (this.photoFile) {
        this.userService.uploadPhoto(this.photoFile, this.userId!).subscribe(() => {
          const mailValue: string = mail ?? '';
          if (!mailValue) {
            alert('Le mail est requis.');
            return;
          }

          const oldPass: string = this.profileForm.value.oldPassword ?? '';
          const newPass: string = this.profileForm.value.newPassword ?? '';
          const confirmPass: string = this.profileForm.value.confirmPassword ?? '';

          if (oldPass || newPass || confirmPass) {
            if (!oldPass || !newPass || !confirmPass) {
              alert('Pour changer le mot de passe, remplissez tous les champs.');
              return;
            }
            if (newPass !== confirmPass) {
              alert('Le nouveau mot de passe et sa confirmation ne correspondent pas.');
              return;
            }
          }

          this.updateEmailAndPassword(mailValue, oldPass, newPass);
        });
      } else {
        const mailValue: string = mail ?? '';
        if (!mailValue) {
          alert('Le mail est requis.');
          return;
        }

        const oldPass: string = this.profileForm.value.oldPassword ?? '';
        const newPass: string = this.profileForm.value.newPassword ?? '';
        const confirmPass: string = this.profileForm.value.confirmPassword ?? '';

        if (oldPass || newPass || confirmPass) {
          if (!oldPass || !newPass || !confirmPass) {
            alert('Pour changer le mot de passe, remplissez tous les champs.');
            return;
          }
          if (newPass !== confirmPass) {
            alert('Le nouveau mot de passe et sa confirmation ne correspondent pas.');
            return;
          }
        }

        this.updateEmailAndPassword(mailValue, oldPass, newPass);
      }
    });
  }

  updateEmailAndPassword(mail: string, oldPassword?: string, newPassword?: string) {
    const mailPayload: UpdateMailDTO = { userId: this.userId!, newEmail: mail };
    this.authService.updateEmail(mailPayload).subscribe(() => {
      if (oldPassword && newPassword) {
        const passwordPayload: UpdatePasswordDTO = {
          userId: this.userId!,
          oldPassword: oldPassword,
          newPassword: newPassword,
        };
        this.authService.updatePassword(passwordPayload).subscribe(() => {
          // Plus d'alert, redirection vers page profil
          this.router.navigate(['/profile']);
        }, () => alert('Erreur lors de la mise à jour du mot de passe.'));
      } else {
        this.router.navigate(['/profile']);
      }
    }, () => alert('Erreur lors de la mise à jour de l\'email.'));
  }

  onDeleteAccount() {
    this.showDeleteConfirm = true;
  }

  confirmDelete() {
    // TODO: appeler la suppression compte dans userService (exemple)
    this.userService.deleteAccount(this.userId!).subscribe(() => {
      this.showDeleteConfirm = false;
      this.router.navigate(['/']);
    }, () => alert('Erreur lors de la suppression du compte'));
  }

  cancelDelete() {
    this.showDeleteConfirm = false;
  }
}
