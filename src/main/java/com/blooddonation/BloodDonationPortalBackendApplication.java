package com.blooddonation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BloodDonationPortalBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BloodDonationPortalBackendApplication.class, args);
    }
}