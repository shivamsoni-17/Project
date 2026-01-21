package com.hmsService.service;

import com.hmsService.dto.PayBillRequest;
import com.hmsService.model.Bill;
import com.hmsService.model.Transaction;
import com.hmsService.repository.BillRepository;
import com.hmsService.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BillingService {

    private final BillRepository billRepository;
    private final TransactionRepository transactionRepository;

    public BillingService(BillRepository billRepository, TransactionRepository transactionRepository) {
        this.billRepository = billRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Bill> listBillsForUser(Long userId) {
        return billRepository.findByUserId(userId);
    }

    public Bill getBill(Long billId) {
        return billRepository.findById(billId).orElseThrow(() -> new IllegalArgumentException("Bill not found"));
    }

    @Transactional
    public Bill pay(PayBillRequest request) {
        Bill bill = getBill(request.getBillId());
        BigDecimal total = bill.getAmount().add(bill.getAddCharges());

        Transaction tx = new Transaction();
        tx.setPayDate(LocalDate.now());
        tx.setMode(Transaction.Mode.valueOf(request.getMode().toUpperCase()));
        tx.setAmount(request.getAmount());
        tx.setStatus(Transaction.Status.SUCCESS);
        tx = transactionRepository.save(tx);

        bill.setTransactionId(tx.getId());
        if (request.getAmount().compareTo(total) >= 0) {
            bill.setPayStatus(Bill.PayStatus.PAID);
        } else if (request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            bill.setPayStatus(Bill.PayStatus.PARTIALLY_PAID);
        } else {
            bill.setPayStatus(Bill.PayStatus.UNPAID);
        }
        return billRepository.save(bill);
    }

    public Map<String, Object> invoice(Long billId) {
        Bill bill = getBill(billId);
        Map<String, Object> inv = new HashMap<>();
        inv.put("billId", bill.getId());
        inv.put("reservationId", bill.getReservationId());
        inv.put("amount", bill.getAmount());
        inv.put("addCharges", bill.getAddCharges());
        inv.put("total", bill.getAmount().add(bill.getAddCharges()));
        inv.put("payStatus", bill.getPayStatus().name());
        inv.put("transactionId", bill.getTransactionId());
        inv.put("createdAt", bill.getCreatedAt());
        return inv;
    }

    public List<Bill> search(Long userId, Long reservationId, String status) {
        List<Bill> all = billRepository.findAll();
        return all.stream()
                .filter(b -> reservationId == null || b.getReservationId().equals(reservationId))
                .filter(b -> status == null || status.isBlank() || b.getPayStatus().name().equals(status.replace(" ", "_")))
                .collect(java.util.stream.Collectors.toList());
    }

    public Bill updateStatus(Long billId, String status) {
        Bill bill = getBill(billId);
        bill.setPayStatus(Bill.PayStatus.valueOf(status.toUpperCase()));
        return billRepository.save(bill);
    }

    @Transactional
    public Bill createBillForReservation(Long reservationId, BigDecimal roomPrice, long numberOfNights) {
        // Check if bill already exists for this reservation
        Optional<Bill> existing = billRepository.findByReservationId(reservationId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Calculate total amount: room price * number of nights
        BigDecimal totalAmount = roomPrice.multiply(BigDecimal.valueOf(numberOfNights));
        BigDecimal addCharges = BigDecimal.valueOf(8.00); // Default additional charges

        Bill bill = Bill.builder()
                .reservationId(reservationId)
                .amount(totalAmount)
                .addCharges(addCharges)
                .payStatus(Bill.PayStatus.UNPAID)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        return billRepository.save(bill);
    }
}

