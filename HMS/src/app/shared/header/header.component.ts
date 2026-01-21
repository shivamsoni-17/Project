import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { AuthService, AuthResponse } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  session: AuthResponse | null = null;

  constructor(private auth: AuthService, private router: Router) {
    this.session = this.auth.getSession();
  }

  isCustomer() { return this.session?.role?.toLowerCase() === 'customer'; }
  isAdmin() { return this.session?.role?.toLowerCase() === 'admin'; }
  isStaff() { return this.session?.role?.toLowerCase() === 'staff'; }

  logout() {
    this.auth.logout();
    this.session = null;
    this.router.navigate(['/login']);
  }
}
