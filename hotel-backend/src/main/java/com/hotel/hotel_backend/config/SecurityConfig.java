package com.hotel.hotel_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // 1. Javne rute za goste (bez prijave)
                .requestMatchers(HttpMethod.GET, "/api/reservations/availability").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/reservations").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/rooms").permitAll()
                .requestMatchers("/api/reservations/**").permitAll()
                
                // 2. Admin rute (zahtijevaju prijavu)
                .requestMatchers("/api/admin/**").authenticated()
                
                // Sve ostalo dopušteno
                .anyRequest().permitAll()
            )
            // Google OAuth2 prijava
            .oauth2Login(Customizer.withDefaults());

        return http.build();
    }
}