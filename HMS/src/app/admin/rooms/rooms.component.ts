import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';

@Component({
  selector: 'app-admin-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rooms.component.html',
  styleUrl: './rooms.component.css'
})
export class AdminRoomsComponent implements OnInit {
  rooms: any[] = [];
  form: any = { roomNo: '', type: 'SINGLE', price: 0, status: 'VACANT', availabilityDate: '' };
  message = '';

  constructor(private roomsService: RoomService) {}

  ngOnInit(): void { this.load(); }

  load() {
    this.roomsService.listAll().subscribe(r => this.rooms = r as any[]);
  }

  save() {
    this.roomsService.save(this.form).subscribe({
      next: () => { this.message = 'Room saved'; this.load(); },
      error: () => this.message = 'Save failed'
    });
  }

  setStatus(roomNo: number, status: string) {
    this.roomsService.updateStatus(roomNo, status).subscribe({
      next: () => this.load(),
      error: () => this.message = 'Update failed'
    });
  }
}
