import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComplaintService } from '../../services/complaint.service';

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

  constructor(private service: ComplaintService) {}

  ngOnInit(): void { this.load(); }

  load() {
    this.service.listAll('').subscribe({
      next: (data: any) => this.complaints = data || [],
      error: () => this.message = 'Load failed'
    });
  }

  setStatus(c: any, status: string) {
    this.service.updateStatus(c.id, status).subscribe({ next: () => this.load() });
  }
}
