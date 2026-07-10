package com.thaddeus.clothing.config;

import com.thaddeus.clothing.entity.Role;
import com.thaddeus.clothing.entity.User;
import com.thaddeus.clothing.entity.Warehouse;
import com.thaddeus.clothing.entity.WarehouseInventory;
import com.thaddeus.clothing.entity.ProductVariant;
import com.thaddeus.clothing.repository.RoleRepository;
import com.thaddeus.clothing.repository.UserRepository;
import com.thaddeus.clothing.repository.WarehouseRepository;
import com.thaddeus.clothing.repository.ProductVariantRepository;
import com.thaddeus.clothing.repository.WarehouseInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final WarehouseRepository warehouseRepository;
    private final ProductVariantRepository productVariantRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing default database seeds...");

        // Ensure roles exist
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    log.info("Creating ROLE_ADMIN...");
                    return roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
                });

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> {
                    log.info("Creating ROLE_CUSTOMER...");
                    return roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build());
                });

        String adminEmail = "admin@thaddeus.vn";
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("Seeding admin user: {}", adminEmail);
            User admin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("Admin@2026!"))
                    .salt(UUID.randomUUID().toString())
                    .fullName("System Admin")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();
            userRepository.save(admin);
        }

        String customer1Email = "customer1@gmail.com";
        if (!userRepository.existsByEmail(customer1Email)) {
            log.info("Seeding customer1 user: {}", customer1Email);
            User customer1 = User.builder()
                    .email(customer1Email)
                    .passwordHash(passwordEncoder.encode("123456"))
                    .salt(UUID.randomUUID().toString())
                    .fullName("Customer One")
                    .phoneNumber("0901234567")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(customerRole)))
                    .build();
            userRepository.save(customer1);
        }

        String customer2Email = "nguyen.van.a@gmail.com";
        if (!userRepository.existsByEmail(customer2Email)) {
            log.info("Seeding customer A user: {}", customer2Email);
            User customer2 = User.builder()
                    .email(customer2Email)
                    .passwordHash(passwordEncoder.encode("Customer@123"))
                    .salt(UUID.randomUUID().toString())
                    .fullName("Nguyen Van A")
                    .phoneNumber("0987654321")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(customerRole)))
                    .build();
            userRepository.save(customer2);
        }

        Warehouse warehouse = warehouseRepository.findById(1L).orElse(null);
        if (warehouse != null) {
            for (ProductVariant v : productVariantRepository.findAll()) {
                if (!warehouseInventoryRepository.findWithLock(warehouse.getId(), v.getId()).isPresent()) {
                    log.info("Auto-seeding default inventory (100 qty) for variant {} in warehouse {}", v.getSku(), warehouse.getName());
                    warehouseInventoryRepository.save(WarehouseInventory.builder()
                            .warehouse(warehouse)
                            .productVariant(v)
                            .physicalQty(100)
                            .allocatedQty(0)
                            .availableToSellQty(100)
                            .build());
                }
            }
        }

        log.info("Database seeds initialized successfully.");
    }
}
