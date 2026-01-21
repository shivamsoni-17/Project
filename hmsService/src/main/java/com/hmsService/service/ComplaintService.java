package com.hmsService.service;

import com.hmsService.dto.ComplaintRequest;
import com.hmsService.model.Complaint;
import com.hmsService.repository.ComplaintRepository;
import com.hmsService.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    public Complaint create(ComplaintRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Complaint complaint = new Complaint();
        complaint.setUserId(request.getUserId());
        complaint.setContact(request.getContact());
        complaint.setRoomNo(request.getRoomNo());
        complaint.setCategory(request.getCategory());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(Complaint.Status.OPEN);
        complaint.setCreatedAt(java.time.LocalDate.now());
        return complaintRepository.save(complaint);
    }

    public List<Complaint> listByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return complaintRepository.findByUser(userId);
    }

    public List<Complaint> listAll(String statusFilter) {
        List<Complaint> all = complaintRepository.findAll();
        if (statusFilter == null || statusFilter.isBlank()) {
            return all;
        }
        Complaint.Status filterStatus = Complaint.Status.valueOf(statusFilter.toUpperCase().replace(" ", "_"));
        return all.stream()
                .filter(c -> c.getStatus().equals(filterStatus))
                .collect(java.util.stream.Collectors.toList());
    }

    public Complaint updateStatus(Long complaintId, String status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        Complaint.Status newStatus = Complaint.Status.valueOf(status.toUpperCase().replace(" ", "_"));
        complaint.setStatus(newStatus);
        if (newStatus == Complaint.Status.RESOLVED || newStatus == Complaint.Status.CLOSED) {
            complaint.setResolvedAt(java.time.LocalDate.now());
        }
        return complaintRepository.save(complaint);
    }
}
