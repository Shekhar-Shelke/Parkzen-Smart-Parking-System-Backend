package com.parkzen.service.impl;

import com.parkzen.dto.request.LoginRequest;
import com.parkzen.dto.request.OwnerRegisterRequest;
import com.parkzen.dto.request.UserRegisterRequest;
import com.parkzen.dto.response.AuthResponse;
import com.parkzen.entity.Admin;
import com.parkzen.entity.Owner;
import com.parkzen.entity.User;
import com.parkzen.exception.DuplicateResourceException;
import com.parkzen.exception.ResourceNotFoundException;
import com.parkzen.exception.UnauthorizedException;
import com.parkzen.repository.AdminRepository;
import com.parkzen.repository.OwnerRepository;
import com.parkzen.repository.UserRepository;
import com.parkzen.security.JwtUtil;
import com.parkzen.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .vehicleNumber(request.getVehicleNumber())
                .mobileNumber(request.getMobileNumber())
                .build();

        user = userRepository.save(user);
        log.info("User registered: {}", user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return buildAuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return buildAuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public AuthResponse registerOwner(OwnerRegisterRequest request) {
        if (ownerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        Owner owner = Owner.builder()
                .name(request.getName())
                .parkingAreaName(request.getParkingAreaName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .approved(false)
                .build();

        owner = ownerRepository.save(owner);
        log.info("Owner registered (pending approval): {}", owner.getEmail());

        String token = jwtUtil.generateToken(owner.getEmail(), owner.getRole().name());
        return buildAuthResponse(token, owner.getId(), owner.getName(), owner.getEmail(), owner.getRole().name());
    }

    @Override
    public AuthResponse loginOwner(LoginRequest request) {
        Owner owner = ownerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), owner.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!owner.getApproved()) {
            throw new UnauthorizedException("Your account is pending admin approval");
        }

        String token = jwtUtil.generateToken(owner.getEmail(), owner.getRole().name());
        return buildAuthResponse(token, owner.getId(), owner.getName(), owner.getEmail(), owner.getRole().name());
    }

    @Override
    public AuthResponse loginAdmin(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No admin account found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole().name());
        return buildAuthResponse(token, admin.getId(), "Admin", admin.getEmail(), admin.getRole().name());
    }

    private AuthResponse buildAuthResponse(String token, Long id, String name, String email, String role) {
        return AuthResponse.builder()
                .token(token)
                .id(id)
                .name(name)
                .email(email)
                .role(role)
                .build();
    }
}
