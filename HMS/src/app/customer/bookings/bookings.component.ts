import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService, BookingRequest } from '../../services/booking.service';
import { AuthService } from '../../services/auth.service';

interface ReservationView {
  id: number;
  roomNo: number;
  checkInDate: string;
  checkOutDate: string;
  status: string;
}

@Component({
  selector: 'app-customer-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bookings.component.html',
  styleUrl: './bookings.component.css'
})
export class CustomerBookingsComponent implements OnInit {

  bookings: ReservationView[] = [];
  message = '';
  filters = { from: '', to: '' };

  constructor(private bookingService: BookingService, private auth: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    const session = this.auth.getSession();
    if (!session) { this.message = 'Login required'; return; }
    this.bookingService.getByUser(session.userId).subscribe({
      next: (data: any) => { this.bookings = data || []; },
      error: () => this.message = 'Unable to load bookings'
    });
  }

  cancel(id: number) {
    this.bookingService.cancel(id).subscribe({
      next: () => this.load(),
      error: () => this.message = 'Cancel failed'
    });
  }

  updateDates(b: ReservationView) {
    const payload: BookingRequest = {
      userId: this.auth.getSession()?.userId!,
      roomNo: b.roomNo,
      checkIn: b.checkInDate,
      checkOut: b.checkOutDate
    };
    this.bookingService.update(b.id, payload).subscribe({
      next: () => this.load(),
      error: () => this.message = 'Update failed'
    });
  }
}
