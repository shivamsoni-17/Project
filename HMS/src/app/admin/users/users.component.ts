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
}
