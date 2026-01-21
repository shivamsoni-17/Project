import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import { AdminRoomsComponent } from '../rooms/rooms.component';
import { AdminReservationsComponent } from '../reservations/reservations.component';
import { AdminUsersComponent } from '../users/users.component';
import { AdminBillsComponent } from '../bills/bills.component';
import { AdminComplaintsComponent } from '../complaints/complaints.component';
import { AdminService } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    AdminRoomsComponent,
    AdminReservationsComponent,
    AdminUsersComponent,
    AdminBillsComponent,
    AdminComplaintsComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  activeTab: 'dashboard' | 'rooms' | 'reservations' | 'users' | 'bills' | 'complaints' = 'dashboard';

  cards = [
    { label: 'Total Rooms', value: '-' },
    { label: 'Vacant', value: '-' },
    { label: 'Occupied', value: '-' },
    { label: 'Reservations', value: '-' },
    { label: 'Open Complaints', value: '-' },
    { label: 'Unpaid Bills', value: '-' }
  ];

  loading = true;
  error = '';

  constructor(
    private adminService: AdminService,
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Add a small delay to ensure component is fully initialized
    setTimeout(() => {
      this.loadStats();
    }, 100);
  }

  loadStats(): void {
    this.loading = true;
    this.error = '';
    
    this.adminService.getStats().pipe(
      catchError((err) => {
        console.error('Failed to load stats:', err);
        this.error = 'Failed to load statistics. Please check your connection and try again.';
        // Return default stats on error
        return of({
          totalRooms: 0,
          vacantRooms: 0,
          occupiedRooms: 0,
          totalReservations: 0,
          openComplaints: 0,
          unpaidBills: 0
        });
      }),
      finalize(() => {
        this.loading = false;
      })
    ).subscribe({
      next: (stats) => {
        console.log('Stats received:', stats);
        this.cards = [
          { label: 'Total Rooms', value: (stats?.totalRooms ?? 0).toString() },
          { label: 'Vacant', value: (stats?.vacantRooms ?? 0).toString() },
          { label: 'Occupied', value: (stats?.occupiedRooms ?? 0).toString() },
          { label: 'Reservations', value: (stats?.totalReservations ?? 0).toString() },
          { label: 'Open Complaints', value: (stats?.openComplaints ?? 0).toString() },
          { label: 'Unpaid Bills', value: (stats?.unpaidBills ?? 0).toString() }
        ];
        this.cdr.markForCheck();
      }
    });
  }

  setTab(tab: 'dashboard' | 'rooms' | 'reservations' | 'users' | 'bills' | 'complaints') {
    this.activeTab = tab;
    if (tab === 'dashboard') {
      this.loadStats();
    }
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
