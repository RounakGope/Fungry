package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Configuration.UserPrincipal;
import com.fung.fungry.Model.User;
import com.fung.fungry.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByUserMail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + email));
        return new UserPrincipal(user);
    }
}