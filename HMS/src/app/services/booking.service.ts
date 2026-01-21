import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface BookingRequest {
  userId: number;
  roomNo: number;
  checkIn: string;
  checkOut: string;
}

@Injectable({ providedIn: 'root' })
export class BookingService {
  private baseUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  book(request: BookingRequest): Observable<any> {
    return this.http.post(this.baseUrl, request);
  }

  getByUser(userId: number) {
    return this.http.get(`${this.baseUrl}/user/${userId}`);
  }

  cancel(reservationId: number) {
    return this.http.put(`${this.baseUrl}/${reservationId}/cancel`, {});
  }

  update(reservationId: number, request: BookingRequest) {
    return this.http.put(`${this.baseUrl}/${reservationId}`, request);
  }

  search(filters: any) {
    return this.http.get(this.baseUrl, { params: filters });
  }
}
