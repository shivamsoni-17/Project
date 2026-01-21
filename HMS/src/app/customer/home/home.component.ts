import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RoomService } from '../../services/room.service';
import { Router } from '@angular/router';
import { Room } from '../../models/room';
import { BookingService } from '../../services/booking.service';
import { AuthService } from '../../services/auth.service';
import { CustomerBookingsComponent } from '../bookings/bookings.component';
import { CustomerComplaintsComponent } from '../complaints/complaints.component';
import { CustomerProfileComponent } from '../profile/profile.component';
import { CustomerBillsComponent } from '../bills/bills.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  imports: [CommonModule, FormsModule, CustomerBookingsComponent, CustomerComplaintsComponent, CustomerProfileComponent, CustomerBillsComponent]
})
export class HomeComponent implements OnInit {

  rooms: Room[] = [];
  message = '';
  activeTab: 'search' | 'bookings' | 'bills' | 'complaints' | 'profile' = 'search';

  searchData = {
    checkIn: '',
    checkOut: '',
    type: ''
  };

  constructor(private roomService: RoomService,
              private bookingService: BookingService,
              private auth: AuthService,
              private router: Router) {}

  ngOnInit(): void {
    this.loadAvailableRooms();
  }

  loadAvailableRooms() {
    this.roomService.getAvailableRooms().subscribe((data: Room[]) => {
      this.rooms = data;
    });
  }

  searchRooms() {
    this.message = '';
    if (!this.searchData.checkIn || !this.searchData.checkOut || !this.searchData.type) {
      this.message = 'Please fill all search fields';
      return;
    }

    this.roomService.searchRooms(
      this.searchData.checkIn,
      this.searchData.checkOut,
      this.searchData.type
    ).subscribe((data: Room[]) => {
      this.rooms = data;
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  bookRoom(roomNo: number) {
    this.message = '';
    const session = this.auth.getSession();
    if (!session) {
      this.message = 'Please login to book';
      this.router.navigate(['/login']);
      return;
    }
    if (!this.searchData.checkIn || !this.searchData.checkOut) {
      this.message = 'Select check-in and check-out dates first';
      return;
    }
    this.bookingService.book({
      userId: session.userId,
      roomNo,
      checkIn: this.searchData.checkIn,
      checkOut: this.searchData.checkOut
    }).subscribe({
      next: () => this.message = 'Room booked successfully!',
      error: (err) => this.message = err?.error?.message || 'Booking failed'
    });
  }

  setTab(tab: 'search' | 'bookings' | 'bills' | 'complaints' | 'profile') {
    this.activeTab = tab;
    this.message = '';
  }
}
