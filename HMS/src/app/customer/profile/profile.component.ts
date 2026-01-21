import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-customer-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class CustomerProfileComponent {
  form = { fullName: '', mobile: '', email: '', username: '', password: '' };
  message = '';

  constructor(private users: UserService, private auth: AuthService) {
    const session = this.auth.getSession();
    if (session) {
      this.users.getById(session.userId).subscribe((u: any) => {
        this.form.fullName = u.fullName || '';
        this.form.mobile = u.mobile || '';
        this.form.email = u.email || '';
        this.form.username = u.username || session.username || '';
      });
    }
  }

  save() {
    const session = this.auth.getSession();
    if (!session) { this.message = 'Login required'; return; }
    this.users.update(session.userId, this.form).subscribe({
      next: () => this.message = 'Profile updated',
      error: () => this.message = 'Update failed'
    });
  }
}
