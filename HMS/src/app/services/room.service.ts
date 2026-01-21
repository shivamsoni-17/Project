import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Room } from '../models/room';

@Injectable({ providedIn: 'root' })
export class RoomService {

  private baseUrl = 'http://localhost:8080/api/rooms';

  constructor(private http: HttpClient) {}

  getAvailableRooms() {
    return this.http.get<Room[]>(`${this.baseUrl}/available`);
  }

  searchRooms(checkIn: string, checkOut: string, type: string) {
    return this.http.get<Room[]>(`${this.baseUrl}/search`, {
      params: {
        checkIn,
        checkOut,
        type
      }
    });
  }

  listAll() {
    return this.http.get<Room[]>(this.baseUrl);
  }

  save(room: any) {
    return this.http.post(this.baseUrl, room);
  }

  updateStatus(roomNo: number, status: string) {
    return this.http.put(`${this.baseUrl}/${roomNo}/status`, { status });
  }
}
