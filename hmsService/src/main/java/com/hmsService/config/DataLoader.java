package com.hmsService.config;

import com.hmsService.model.Complaint;
import com.hmsService.model.Room;
import com.hmsService.model.User;
import com.hmsService.repository.ComplaintRepository;
import com.hmsService.repository.RoomRepository;
import com.hmsService.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedData(RoomRepository roomRepository, 
                                UserRepository userRepository,
                                ComplaintRepository complaintRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed rooms
            if (roomRepository.count() == 0) {
                List<Room> rooms = Arrays.asList(
                        new Room(101, Room.Type.SINGLE, BigDecimal.valueOf(1200), Room.Status.VACANT, LocalDate.now()),
                        new Room(102, Room.Type.DOUBLE, BigDecimal.valueOf(1800), Room.Status.VACANT, LocalDate.now()),
                        new Room(201, Room.Type.SUITE, BigDecimal.valueOf(3500), Room.Status.VACANT, LocalDate.now())
                );
                for (Room room : rooms) {
                    roomRepository.save(room);
                }
            }

            // Seed hardcoded admin user
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setFullName("System Administrator");
                admin.setEmail("admin@hms.com");
                admin.setMobile("9999999999");
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("admin");
                admin.setCreatedAt(LocalDateTime.now());
                userRepository.save(admin);
            }

            // Seed hardcoded staff user
            if (!userRepository.existsByUsername("staff")) {
                User staff = new User();
                staff.setFullName("Staff Member");
                staff.setEmail("staff@hms.com");
                staff.setMobile("8888888888");
                staff.setUsername("staff");
                staff.setPassword(passwordEncoder.encode("staff123"));
                staff.setRole("staff");
                staff.setCreatedAt(LocalDateTime.now());
                userRepository.save(staff);
            }

            // Seed test customer user
            if (!userRepository.existsByUsername("customer1")) {
                User customer = new User();
                customer.setFullName("John Doe");
                customer.setEmail("john@example.com");
                customer.setMobile("7777777777");
                customer.setUsername("customer1");
                customer.setPassword(passwordEncoder.encode("customer123"));
                customer.setRole("customer");
                customer.setCreatedAt(LocalDateTime.now());
                userRepository.save(customer);
            }

            // Seed test complaints if none exist
            if (complaintRepository.findAll().isEmpty()) {
                User customer = userRepository.findByUsername("customer1").orElse(null);
                if (customer != null) {
                    Complaint complaint1 = new Complaint();
                    complaint1.setUserId(customer.getId());
                    complaint1.setContact(customer.getMobile());
                    complaint1.setRoomNo(101);
                    complaint1.setCategory("Maintenance");
                    complaint1.setDescription("Air conditioning is not working properly in the room");
                    complaint1.setStatus(Complaint.Status.OPEN);
                    complaint1.setCreatedAt(LocalDate.now());
                    complaintRepository.save(complaint1);

                    Complaint complaint2 = new Complaint();
                    complaint2.setUserId(customer.getId());
                    complaint2.setContact(customer.getMobile());
                    complaint2.setRoomNo(102);
                    complaint2.setCategory("Cleaning");
                    complaint2.setDescription("Room needs cleaning service");
                    complaint2.setStatus(Complaint.Status.IN_PROGRESS);
                    complaint2.setCreatedAt(LocalDate.now().minusDays(1));
                    complaintRepository.save(complaint2);

                    Complaint complaint3 = new Complaint();
                    complaint3.setUserId(customer.getId());
                    complaint3.setContact(customer.getMobile());
                    complaint3.setRoomNo(201);
                    complaint3.setCategory("Amenities");
                    complaint3.setDescription("WiFi password is not working");
                    complaint3.setStatus(Complaint.Status.RESOLVED);
                    complaint3.setCreatedAt(LocalDate.now().minusDays(2));
                    complaint3.setResolvedAt(LocalDate.now().minusDays(1));
                    complaintRepository.save(complaint3);
                }
            }
        };
    }
}
