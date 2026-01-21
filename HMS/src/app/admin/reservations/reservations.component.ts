import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService, BookingRequest } from '../../services/booking.service';

@Component({
  selector: 'app-admin-reservations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reservations.component.html',
  styleUrl: './reservations.component.css'
})
export class AdminReservationsComponent implements OnInit {
  reservations: any[] = [];
  filters = { userId: '', roomNo: '', from: '', to: '' };
  message = '';

  constructor(private bookings: BookingService) {}

  ngOnInit(): void { this.load(); }

  load() {
    this.bookings.search(this.filters).subscribe({
      next: (data: any) => this.reservations = data || [],
      error: () => this.message = 'Load failed'
    });
  }

  cancel(id: number) {
    this.bookings.cancel(id).subscribe({ next: () => this.load() });
  }

  update(r: any) {
    const payload: BookingRequest = {
      userId: r.userId,
      roomNo: r.roomNo,
      checkIn: r.checkInDate,
      checkOut: r.checkOutDate
    };
    this.bookings.update(r.id, payload).subscribe({ next: () => this.load() });
  }
}
