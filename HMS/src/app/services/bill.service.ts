import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface PayBillRequest {
  billId: number;
  mode: string;
  amount: number;
}

@Injectable({ providedIn: 'root' })
export class BillService {
  private baseUrl = 'http://localhost:8080/api/bills';

  constructor(private http: HttpClient) {}

  // Customer methods
  getBillsForUser(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/user/${userId}`);
  }

  payBill(request: PayBillRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/pay`, request);
  }

  getInvoice(billId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${billId}/invoice`);
  }

  // Admin methods
  search(filters: any) {
    return this.http.get(this.baseUrl, { params: filters });
  }

  updateStatus(billId: number, status: string) {
    return this.http.put(`${this.baseUrl}/${billId}/status`, { status });
  }
}
