package org.example.mock.Sercurity;

import org.example.mock.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SercurityConfiguration {

    @Autowired
    @Lazy
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authenticationProvider(customAuthenticationProvider)
                .authorizeRequests(authz -> authz
                        .requestMatchers("/login", "/home", "/forgot", "/recovery", "/newpass","/filterCandidates","/cancelCandidate","/interviewschedules/detail","/interviewschedules/create",
                                "/interviewschedules/markAsInterviewed", "/interviewschedules/deleteInterviewed","/admin/getForm", "/admin/createAccount", "/admin/AccountList", "/admin/addAccount",
                                "/admin/getUpdateForm/{id}","/admin/getUpdateForm", "/admin/update", "/joblist", "/Manager/viewJob/{id}","/ApproveReject/jobList", "/ApproveReject/offers","/ApproveReject/viewJob/{id}", "/ApproveReject/approveJob/{id}",
                                "/ApproveReject/rejectJob/{id}","/Manager/viewJob/{id}", "/ApproveReject/viewOffer/{id}", "/ApproveReject/approveOffer/{id}", "/ApproveReject/rejectOffer/{id}", "/users", "/offers", "/offers/{id}/detail", "/offers/update", "/offers/create", "/profile", "/profile/editprofile", "/changepassword/*", "/changepassword/submit").permitAll() // Allow unauthenticated access
                        .requestMatchers("/admin/**").hasRole("ADMIN")        // Admin access
                        .requestMatchers("/recruiter/**").hasRole("RECRUITER") // Recruiter access
                        .requestMatchers("/manager/**").hasRole("MANAGER")     // Manager access
                        .requestMatchers("/interviewer/**").hasRole("INTERVIEWER") // Interviewer access
                        .anyRequest().authenticated()                          // All other requests require authentication
                )
//                .formLogin(form -> form
//                        .loginPage("/login")                                  // URL for the login page
//                        .permitAll()                                          // Allow everyone to access the login page
//                        .successHandler(authenticationSuccessHandler())       // Custom success handler
//                        .failureHandler(authenticationFailureHandler())       // Custom failure handler
//                )
                .logout(logout -> logout
                        .permitAll().logoutSuccessUrl("/login")                           // Redirect to login page after logout
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")                   // Custom 403 error page
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            User user = (User) authentication.getPrincipal();
            String role = user.getRole().toUpperCase();

            // Redirect based on the user's role
            switch (role) {
                case "ADMIN":
                    response.sendRedirect("/admin/dashboard");
                    break;
                case "RECRUITER":
                    response.sendRedirect("/recruiter/dashboard");
                    break;
                case "MANAGER":
                    response.sendRedirect("/manager/dashboard");
                    break;
                case "INTERVIEWER":
                    response.sendRedirect("/interviewer/dashboard");
                    break;
                default:
                    response.sendRedirect("/default");
                    break;
            }
        };
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.sendRedirect("/login?error=true"); // Redirect to login page with error message on failure
        };
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }
}