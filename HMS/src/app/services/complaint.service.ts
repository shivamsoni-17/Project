import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

export interface ComplaintPayload {
  userId: number;
  contact: string;
  category: string;
  description: string;
  roomNo?: number;
}

@Injectable({ providedIn: 'root' })
export class ComplaintService {
  private baseUrl = 'http://localhost:8080/api/complaints';

  constructor(private http: HttpClient) {}

  create(payload: ComplaintPayload) {
    return this.http.post(this.baseUrl, payload);
  }

  listByUser(userId: number) {
    return this.http.get(`${this.baseUrl}/user/${userId}`);
  }

  listAll(status?: string) {
    const params: any = {};
    if (status) { params.status = status; }
    return this.http.get(this.baseUrl, { params });
  }

  updateStatus(id: number, status: string) {
    return this.http.put(`${this.baseUrl}/${id}/status`, { status });
  }
}
