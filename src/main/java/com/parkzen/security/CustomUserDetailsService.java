package com.parkzen.security;

import com.parkzen.entity.User;
import com.parkzen.repository.UserRepository;
import com.parkzen.repository.OwnerRepository;
import com.parkzen.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository  userRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Check users table first (most common)
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            var u = userOpt.get();
            return build(u.getEmail(), u.getPassword(), u.getRole().name());
        }

        // 2. Check owners table
        var ownerOpt = ownerRepository.findByEmail(email);
        if (ownerOpt.isPresent()) {
            var o = ownerOpt.get();
            return build(o.getEmail(), o.getPassword(), o.getRole().name());
        }

        // 3. Check admins table last (least common)
        var adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            var a = adminOpt.get();
            return build(a.getEmail(), a.getPassword(), a.getRole().name());
        }

        throw new UsernameNotFoundException("No account found with email: " + email);
    }

    private UserDetails build(String email, String password, String role) {
        return new org.springframework.security.core.userdetails.User(
                email,
                password,
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
