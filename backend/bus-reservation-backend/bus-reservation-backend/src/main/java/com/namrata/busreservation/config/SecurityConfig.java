package com.namrata.busreservation.config;

import com.namrata.busreservation.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }


    // ==========================================================
    // SECURITY FILTER CHAIN
    // ==========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // ================= CORS =================

            .cors(cors -> cors.configurationSource(
                    corsConfigurationSource()
            ))


            // ================= CSRF =================

            .csrf(csrf -> csrf.disable())


            // ================= SESSION =================

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )


            // ================= AUTHORIZATION =================

            .authorizeHttpRequests(auth -> auth


                // ==================================================
                // USER AUTHENTICATION
                // ==================================================

                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login"
                ).permitAll()


                // ==================================================
                // CORS PREFLIGHT
                // ==================================================

                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()


                // ==================================================
                // ADMIN
                // ==================================================

                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")


                // ==================================================
                // BUS
                // ==================================================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/buses"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/buses/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/buses/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/buses",
                    "/api/buses/**"
                ).authenticated()


                // ==================================================
                // ROUTES
                // ==================================================

                // Public bus search
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/routes/search"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/routes"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/routes/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/routes/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/routes",
                    "/api/routes/**"
                ).authenticated()


                // ==================================================
                // SEATS
                // ==================================================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/seats"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/seats/generate/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/seats/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/seats/**"
                ).hasRole("ADMIN")

                // Public seat viewing
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/seats/bus/**"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/seats",
                    "/api/seats/**"
                ).authenticated()


                // ==================================================
                // BOOKINGS
                // ==================================================

                // Create booking - logged-in user
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/bookings"
                ).authenticated()

                // User's bookings
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/bookings/my",
                    "/api/bookings/my/**"
                ).authenticated()


                // ==================================================
                // ADMIN BOOKING ACCESS
                // ==================================================

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/bookings"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/bookings/{bookingId}"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/bookings/user/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/bookings/route/**"
                ).hasRole("ADMIN")


                // ==================================================
                // USER CANCEL BOOKING
                // ==================================================

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/bookings/*/cancel"
                ).authenticated()


                // ==================================================
                // ADMIN CANCEL BOOKING
                // ==================================================

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/bookings/admin/**"
                ).hasRole("ADMIN")


                // ==================================================
                // PAYMENTS
                // ==================================================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/payments"
                ).authenticated()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/payments"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/payments/{paymentId}"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/payments/booking/**"
                ).authenticated()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/payments/transaction/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/payments/status/**"
                ).hasRole("ADMIN")


                // ==================================================
                // EVERYTHING ELSE
                // ==================================================

                .anyRequest().authenticated()
            )


            // ================= DISABLE FORM LOGIN =================

            .formLogin(form ->
                form.disable()
            )


            // ================= DISABLE BASIC AUTH =================

            .httpBasic(basic ->
                basic.disable()
            )


            // ================= JWT FILTER =================

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );


        return http.build();
    }


    // ==========================================================
    // CORS CONFIGURATION
    // ==========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                    "http://127.0.0.1:5500",
                    "http://localhost:5500"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "DELETE",
                    "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                    "Authorization",
                    "Content-Type"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // ==========================================================
    // PASSWORD ENCODER
    // ==========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}