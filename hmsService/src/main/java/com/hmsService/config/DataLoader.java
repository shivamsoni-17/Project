package com.hmsService.config;

import com.hmsService.model.Room;
import com.hmsService.model.User;
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
        };
    }
}
