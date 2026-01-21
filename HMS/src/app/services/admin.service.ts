import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, timeout, catchError, of } from 'rxjs';

export interface AdminStats {
  totalRooms: number;
  vacantRooms: number;
  occupiedRooms: number;
  totalReservations: number;
  openComplaints: number;
  unpaidBills: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private baseUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.baseUrl}/stats`).pipe(
      timeout(10000), // 10 second timeout
      catchError((err) => {
        console.error('AdminService getStats error:', err);
        // Return default stats on error
        return of({
          totalRooms: 0,
          vacantRooms: 0,
          occupiedRooms: 0,
          totalReservations: 0,
          openComplaints: 0,
          unpaidBills: 0
        });
      })
    );
  }
}
