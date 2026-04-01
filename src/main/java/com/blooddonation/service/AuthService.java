// blood-donation-portal-backend/src/main/java/com/blooddonation/service/AuthService.java
package com.blooddonation.service;

import com.blooddonation.dto.request.RegisterRequest;
import com.blooddonation.dto.request.LoginRequest;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.AuthResponse;
import com.blooddonation.entity.User;
import com.blooddonation.entity.DonorProfile;
import com.blooddonation.entity.Hospital;
import com.blooddonation.enums.InstitutionType;
import com.blooddonation.enums.Role;
import com.blooddonation.enums.VerificationStatus;
import com.blooddonation.repository.UserRepository;
import com.blooddonation.repository.DonorProfileRepository;
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.security.JwtTokenProvider;
import com.blooddonation.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepo;
    private final DonorProfileRepository donorRepo;
    private final HospitalRepository hospitalRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;

    public ApiResponse<String> register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new BusinessException("Email already in use");
        }

        // Save base User with location data included
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(req.getRole())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .city(req.getCity())
                .state(req.getState())
                .enabled(true)
                .build();

        userRepo.save(user);

        // Save Donor Profile
        if (req.getRole() == Role.DONOR) {
            DonorProfile profile = DonorProfile.builder()
                    .user(user)
                    .name(req.getName())
                    .bloodGroup(req.getBloodGroup())
                    .phone(req.getPhone())
                    .latitude(req.getLatitude())
                    .longitude(req.getLongitude())
                    .city(req.getCity())
                    .state(req.getState())
                    // Must set explicitly because @Builder ignores field initializers
                    .verificationStatus(VerificationStatus.UNVERIFIED)
                    .build();

            donorRepo.save(profile);

            // Save Hospital Profile
        } else if (req.getRole() == Role.HOSPITAL) {
            Hospital hospital = Hospital.builder()
                    .user(user)
                    .hospitalName(req.getHospitalName())
                    .licenseNumber(req.getLicenseNumber())
                    .address(req.getAddress())
                    .latitude(req.getLatitude())
                    .longitude(req.getLongitude())
                    .city(req.getCity())
                    .state(req.getState())
                    // Must set explicitly because @Builder ignores field initializers
                    .verificationStatus(VerificationStatus.PENDING)
                    .institutionType(InstitutionType.HOSPITAL)
                    .build();

            hospitalRepo.save(hospital);
        }

        return ApiResponse.ok("Registration successful", null);
    }

    public ApiResponse<AuthResponse> login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        String token = tokenProvider.generateToken(auth);

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow();

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();

        return ApiResponse.ok("Login successful", response);
    }
}