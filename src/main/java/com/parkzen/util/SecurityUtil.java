package com.parkzen.util;

import com.parkzen.entity.Admin;
import com.parkzen.entity.Owner;
import com.parkzen.entity.User;
import com.parkzen.exception.ResourceNotFoundException;
import com.parkzen.exception.UnauthorizedException;
import com.parkzen.repository.AdminRepository;
import com.parkzen.repository.OwnerRepository;
import com.parkzen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user found");
        }
        return authentication.getName();
    }

    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public Owner getCurrentOwner() {
        String email = getCurrentUserEmail();
        return ownerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with email: " + email));
    }

    public Admin getCurrentAdmin() {
        String email = getCurrentUserEmail();
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with email: " + email));
    }
}
