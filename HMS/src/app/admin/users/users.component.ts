import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class AdminUsersComponent implements OnInit {
  users: any[] = [];
  filter = '';
  message = '';
  successMessage = '';

  constructor(private userService: UserService) {}

  ngOnInit(): void { this.load(); }

  load() {
    this.userService.list(this.filter).subscribe({
      next: (data: any) => this.users = data || [],
      error: () => this.message = 'Load failed'
    });
  }

  setRole(u: any, role: string) {
    this.userService.update(u.id, { role }).subscribe({ next: () => this.load() });
  }

  deleteUser(u: any) {
    if (confirm(`Are you sure you want to delete user '${u.username}'? This action cannot be undone.`)) {
      this.userService.delete(u.id).subscribe({
        next: () => {
          this.successMessage = `User '${u.username}' deleted successfully`;
          setTimeout(() => this.successMessage = '', 3000);
          this.load();
        },
        error: (err) => {
          this.message = err.error?.message || 'Failed to delete user';
        }
      });
    }
  }
}
