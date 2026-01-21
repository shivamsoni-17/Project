import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ComplaintService, ComplaintPayload } from '../../services/complaint.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-customer-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaints.component.html',
  styleUrl: './complaints.component.css'
})
export class CustomerComplaintsComponent implements OnInit {

  complaints: any[] = [];
  form: ComplaintPayload = { userId: 0, contact: '', category: '', description: '', roomNo: undefined };
  message = '';

  constructor(private service: ComplaintService, private auth: AuthService) {}

  ngOnInit(): void {
    const session = this.auth.getSession();
    if (session) {
      this.form.userId = session.userId;
      this.load();
    }
  }

  load() {
    const session = this.auth.getSession();
    if (!session) { this.message = 'Login required'; return; }
    this.service.listByUser(session.userId).subscribe({
      next: (data: any) => this.complaints = data || [],
      error: () => this.message = 'Unable to load complaints'
    });
  }

  submit() {
    this.message = '';
    const session = this.auth.getSession();
    if (!session) { this.message = 'Login required'; return; }
    this.form.userId = session.userId;
    this.service.create(this.form).subscribe({
      next: () => { this.message = 'Submitted'; this.form.category=''; this.form.description=''; this.form.roomNo=undefined; this.load(); },
      error: () => this.message = 'Submit failed'
    });
  }
}
