import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BillService } from '../../services/bill.service';

@Component({
  selector: 'app-admin-bills',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bills.component.html',
  styleUrl: './bills.component.css'
})
export class AdminBillsComponent implements OnInit {
  bills: any[] = [];
  filters = { userId: '', reservationId: '', status: '' };
  message = '';

  constructor(private billService: BillService) {}

  ngOnInit(): void { this.load(); }

  load() {
    this.billService.search(this.filters).subscribe({
      next: (data: any) => this.bills = data || [],
      error: () => this.message = 'Load failed'
    });
  }

  setStatus(billId: number, status: string) {
    this.billService.updateStatus(billId, status).subscribe({ next: () => this.load() });
  }

  invoice(billId: number) {
    this.billService.getInvoice(billId).subscribe();
  }
}
