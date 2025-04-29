package com.CSIT321.Hkotisk.Config;

import com.CSIT321.Hkotisk.Filter.JwtFilter;
import com.CSIT321.Hkotisk.Service.MyUserDetailService;
import com.CSIT321.Hkotisk.Handler.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private MyUserDetailService userDetailsService;

    @Autowired
    private JwtFilter jwtAuthFilter;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(request -> request
                .requestMatchers("/resources/**", "/static/**", "/public/**", 
                    "/auth/**", "/auth/signup", "/auth/signin", 
                    "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", 
                    "/ws/**", "/topic/**", "/oauth2/**", 
                    "/login/oauth2/code/google", "/login/oauth2/code/microsoft")
                    .permitAll()
                .requestMatchers("/user/**", "/auth/role", 
                    "/user/products", "/user/addToCart", "/user/cart", 
                    "/user/order").hasAnyAuthority("ROLE_STUDENT", "ROLE_STAFF")
                .requestMatchers("/staff/**", "/staff/product", 
                    "/staff/orders", "/staff/order").hasAuthority("ROLE_STAFF")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/signin")
                .successHandler(oAuth2LoginSuccessHandler)
                .failureUrl("/auth/login-failed")
                .failureHandler((request, response, exception) -> {
                    System.out.println("OAuth2 failure: " + exception.getMessage());
                    exception.printStackTrace();
                    response.sendRedirect("/auth/login-failed?error=" + exception.getMessage());
                })
            )
            .sessionManagement(manager -> manager
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
