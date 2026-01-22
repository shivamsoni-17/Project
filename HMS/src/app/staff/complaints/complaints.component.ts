import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StaffService } from '../../services/staff.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-staff-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaints.component.html',
  styleUrl: './complaints.component.css'
})
export class StaffComplaintsComponent implements OnInit {
  complaints: any[] = [];
  message = '';
  successMessage = '';
  filterStatus = '';
  isLoading = false;

  constructor(
    private staffService: StaffService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.isLoading = true;
    this.message = '';
    this.successMessage = '';
    this.staffService.getAllComplaints(this.filterStatus).subscribe({
      next: (data: any) => {
        this.complaints = data || [];
        this.isLoading = false;
      },
      error: (err) => {
        this.message = 'Failed to load complaints. Please try again.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  setStatus(c: any, status: string) {
    this.staffService.updateComplaintStatus(c.id, status).subscribe({
      next: () => {
        this.successMessage = `Complaint status updated to ${status}`;
        setTimeout(() => this.successMessage = '', 3000);
        this.load();
      },
      error: (err) => {
        this.message = 'Failed to update complaint status';
        console.error(err);
      }
    });
  }

  onFilterChange() {
    this.load();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
