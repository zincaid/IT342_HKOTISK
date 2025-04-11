package com.CSIT321.Hkotisk.Service;

import java.util.Optional;

import com.CSIT321.Hkotisk.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.CSIT321.Hkotisk.Repository.UserRepository;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isPresent()){
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getEmail())
                    .password(user.get().getPassword())
                    .authorities("ROLE_"+user.get().getRole().toUpperCase())
                    .build();
        }else{
            throw new UsernameNotFoundException(email);
        }
    }
}