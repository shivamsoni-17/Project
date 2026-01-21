import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [FormsModule, RouterLink]
})
export class LoginComponent {

  username = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.error = '';
    this.auth.login(this.username, this.password).subscribe({
      next: (response) => {
        const session = this.auth.getSession();
        if (session) {
          const role = session.role?.toLowerCase();
          if (role === 'admin') {
            this.router.navigate(['/admin/dashboard']);
          } else if (role === 'staff') {
            this.router.navigate(['/staff/complaints']);
          } else {
            this.router.navigate(['/customer/home']);
          }
        } else {
          this.router.navigate(['/customer/home']);
        }
      },
      error: (err) => {
        this.error = 'Invalid credentials';
      }
    });
  }
}
