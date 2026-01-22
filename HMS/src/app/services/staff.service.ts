import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ComplaintDetail {
  id: number;
  userId: number;
  userName?: string;
  roomNo?: number;
  category: string;
  description: string;
  status: string;
  createdAt: string;
  updatedAt?: string;
  contact?: string;
}

@Injectable({ providedIn: 'root' })
export class StaffService {
  private baseUrl = 'http://localhost:8080/api/complaints';

  constructor(private http: HttpClient) {}

  // Get all complaints for staff to manage
  getAllComplaints(status?: string): Observable<ComplaintDetail[]> {
    const params: any = {};
    if (status) {
      params.status = status;
    }
    return this.http.get<ComplaintDetail[]>(this.baseUrl, { params });
  }

  // Update complaint status by staff
  updateComplaintStatus(complaintId: number, status: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${complaintId}/status`, { status });
  }

  // Get specific complaint details
  getComplaintDetails(complaintId: number): Observable<ComplaintDetail> {
    return this.http.get<ComplaintDetail>(`${this.baseUrl}/${complaintId}`);
  }

  // Add note/comment to complaint (if backend supports)
  addComplaintNote(complaintId: number, note: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${complaintId}/notes`, { note });
  }
}
