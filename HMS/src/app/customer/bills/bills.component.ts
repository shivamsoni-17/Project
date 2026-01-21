import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BillService } from '../../services/bill.service';
import { AuthService } from '../../services/auth.service';

interface Bill {
  id: number;
  reservationId: number;
  amount: number;
  addCharges: number;
  payStatus: string;
  transactionId?: number;
  createdAt: string;
}

interface Invoice {
  billId: number;
  reservationId: number;
  amount: number;
  addCharges: number;
  total: number;
  payStatus: string;
  transactionId?: number;
  createdAt: string;
}

@Component({
  selector: 'app-customer-bills',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './bills.component.html',
  styleUrl: './bills.component.css'
})
export class CustomerBillsComponent implements OnInit {

  bills: Bill[] = [];
  message = '';
  selectedBill: Bill | null = null;
  paymentData = {
    amount: 0,
    mode: ''
  };
  invoice: Invoice | null = null;
  showInvoice = false;

  constructor(private billService: BillService, private auth: AuthService) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    const session = this.auth.getSession();
    if (!session) {
      this.message = 'Login required';
      return;
    }
    this.billService.getBillsForUser(session.userId).subscribe({
      next: (data: any) => {
        this.bills = data || [];
        this.message = '';
      },
      error: () => this.message = 'Unable to load bills'
    });
  }

  openPayModal(bill: Bill) {
    this.selectedBill = bill;
    const total = bill.amount + bill.addCharges;
    this.paymentData = {
      amount: total,
      mode: 'UPI'
    };
    this.message = '';
  }

  closePayModal() {
    this.selectedBill = null;
    this.paymentData = { amount: 0, mode: '' };
    this.message = '';
  }

  payBill() {
    if (!this.selectedBill) return;
    
    if (!this.paymentData.mode) {
      this.message = 'Please select payment mode';
      return;
    }

    if (this.paymentData.amount <= 0) {
      this.message = 'Payment amount must be greater than 0';
      return;
    }

    this.billService.payBill({
      billId: this.selectedBill.id,
      mode: this.paymentData.mode,
      amount: this.paymentData.amount
    }).subscribe({
      next: () => {
        this.message = 'Payment successful! Transaction saved.';
        this.closePayModal();
        this.load();
      },
      error: (err) => {
        this.message = err?.error?.message || 'Payment failed';
      }
    });
  }

  generateInvoice(billId: number) {
    this.billService.getInvoice(billId).subscribe({
      next: (data: any) => {
        this.invoice = data;
        this.showInvoice = true;
        this.message = '';
      },
      error: () => this.message = 'Unable to generate invoice'
    });
  }

  closeInvoice() {
    this.showInvoice = false;
    this.invoice = null;
  }

  getTotal(bill: Bill): number {
    return bill.amount + bill.addCharges;
  }

  getStatusClass(status: string): string {
    const s = status?.toLowerCase() || '';
    if (s === 'paid') return 'paid';
    if (s === 'partially_paid') return 'partial';
    if (s === 'unpaid') return 'unpaid';
    return '';
  }
}
