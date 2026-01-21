package com.hmsService.controller;

import com.hmsService.dto.PayBillRequest;
import com.hmsService.model.Bill;
import com.hmsService.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "http://localhost:4200")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    // Customer: list my bills
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bill>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(billingService.listBillsForUser(userId));
    }

    // Customer: view invoice (JSON payload - demo friendly)
    @GetMapping("/{billId}/invoice")
    public ResponseEntity<Map<String, Object>> invoice(@PathVariable Long billId) {
        return ResponseEntity.ok(billingService.invoice(billId));
    }

    // Customer: pay bill
    @PostMapping("/pay")
    public ResponseEntity<Bill> pay(@Valid @RequestBody PayBillRequest request) {
        return ResponseEntity.ok(billingService.pay(request));
    }

    // Admin: search bills
    @GetMapping
    public ResponseEntity<List<Bill>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long reservationId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(billingService.search(userId, reservationId, status));
    }

    // Admin: update bill status
    @PutMapping("/{billId}/status")
    public ResponseEntity<Bill> updateStatus(@PathVariable Long billId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(billingService.updateStatus(billId, request.get("status")));
    }
}

