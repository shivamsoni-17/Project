
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { HomeComponent } from './customer/home/home.component';
import { CustomerBookingsComponent } from './customer/bookings/bookings.component';
import { CustomerComplaintsComponent } from './customer/complaints/complaints.component';
import { CustomerProfileComponent } from './customer/profile/profile.component';
import { AdminDashboardComponent } from './admin/dashboard/dashboard.component';
import { AdminRoomsComponent } from './admin/rooms/rooms.component';
import { AdminReservationsComponent } from './admin/reservations/reservations.component';
import { AdminUsersComponent } from './admin/users/users.component';
import { AdminBillsComponent } from './admin/bills/bills.component';
import { AdminComplaintsComponent } from './admin/complaints/complaints.component';
import { StaffComplaintsComponent } from './staff/complaints/complaints.component';


export const routes: Routes = [
  { path: '', component: LoginComponent },          // FIRST PAGE
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'customer/home', component: HomeComponent },
   // Customer
  { path: 'customer/bookings', component: CustomerBookingsComponent },
  { path: 'customer/complaints', component: CustomerComplaintsComponent },
  { path: 'customer/profile', component: CustomerProfileComponent },
  // Admin
  { path: 'admin/dashboard', component: AdminDashboardComponent },
  { path: 'admin/rooms', component: AdminRoomsComponent },
  { path: 'admin/reservations', component: AdminReservationsComponent },
  { path: 'admin/users', component: AdminUsersComponent },
  { path: 'admin/bills', component: AdminBillsComponent },
  { path: 'admin/complaints', component: AdminComplaintsComponent },
  // Staff
  { path: 'staff/complaints', component: StaffComplaintsComponent },
  { path: '**', redirectTo: '' }                     // fallback
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}

