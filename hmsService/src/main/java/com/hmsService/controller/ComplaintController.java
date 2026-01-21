package com.hmsService.controller;

import com.hmsService.dto.ComplaintRequest;
import com.hmsService.model.Complaint;
import com.hmsService.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:4200")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<Complaint> create(@Valid @RequestBody ComplaintRequest request) {
        return ResponseEntity.ok(complaintService.create(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Complaint>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(complaintService.listByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> listAll(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(complaintService.listAll(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Complaint> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(complaintService.updateStatus(id, request.get("status")));
    }
}
