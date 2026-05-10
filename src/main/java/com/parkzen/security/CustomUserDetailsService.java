package com.parkzen.security;

import com.parkzen.entity.Admin;
import com.parkzen.entity.Owner;
import com.parkzen.entity.User;
import com.parkzen.repository.AdminRepository;
import com.parkzen.repository.OwnerRepository;
import com.parkzen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check User table
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority(user.getRole().name()))
            );
        }

        // Check Owner table
        Optional<Owner> ownerOpt = ownerRepository.findByEmail(email);
        if (ownerOpt.isPresent()) {
            Owner owner = ownerOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    owner.getEmail(),
                    owner.getPassword(),
                    List.of(new SimpleGrantedAuthority(owner.getRole().name()))
            );
        }

        // Check Admin table
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPassword(),
                    List.of(new SimpleGrantedAuthority(admin.getRole().name()))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
