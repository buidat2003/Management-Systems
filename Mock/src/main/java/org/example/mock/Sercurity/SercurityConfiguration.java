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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
                        // Allow unauthenticated access to these endpoints
                        .requestMatchers("/login", "/home", "/forgot", "/recovery", "/newpass",

                                "/admin/getForm", "/admin/createAccount", "/admin/AccountList", "/admin/addAccount",
                                "/admin/getUpdateForm/{id}", "/admin/getUpdateForm", "/admin/update", "/joblist",
                                "/Manager/viewJob/{id}", "/users","/offers", "/offers/{id}/detail", "/offers/update",
                                "/offers/create", "/profile","/profile/editprofile", "/changepassword/*", "/changepassword/submit",
                                "/vacancy/*", "/submitApplication", "/downloadCV", "/uploadTemporaryFile",
                                "/download/cv/*","/jobcandidate", "/static/**")
                        .permitAll()
                        .requestMatchers("/current-user").authenticated()
                        // Role-based access restrictions

                        .requestMatchers("/admin/**","/admin/getForm", "/admin/createAccount").hasRole("ADMIN")
                        .requestMatchers("/recruiter/**","/filterCandidates","/cancelCandidate","/interviewschedules/create","/manager/editJob/{id}", "/Manager/deleteJob/{id}",
                                "/joblist", "/manager/updateJob", "/Manager/viewJob/{id}","/manager/CreateJob", "/manager/createJob" ).hasRole("RECRUITER")
                        .requestMatchers("/manager/**", "/ApproveReject/**", "/ApproveReject/offers","/ApproveReject/viewOffer/{id}",
                                "/ApproveReject/approveOffer/{id}", "/ApproveReject/rejectOffer/{id}").hasRole("MANAGER")
                        .requestMatchers("/interviewer/**","/interviewschedules/detail","/interviewschedules/markAsInterviewed", "/interviewschedules/deleteInterviewed").hasRole("INTERVIEWER")
                        .anyRequest().authenticated()  // Require authentication for all other requests
                )

                .formLogin(form -> form
                        .loginPage("/login")  // URL for the login page
                        .permitAll()  // Allow everyone to access the login page
                        .successHandler(authenticationSuccessHandler())  // Custom success handler
                        .failureHandler(authenticationFailureHandler())  // Custom failure handler
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessUrl("/login")  // Redirect to login page after logout
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")  // Custom 403 error page
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
            request.getSession().setAttribute("USER_NAME", user.getUsername());
            request.getSession().setAttribute("USER_ID", user.getId());
            request.getSession().setAttribute("USER_ROLE", user.getRole());
            request.getSession().setAttribute("USER_AVATAR", user.getAvatar());
            System.out.println("Logged-in User ID: " + user.getId());
            // Redirect based on the user's role
            switch (role) {
                case "ADMIN":
                    response.sendRedirect("/admin/dashboard");
                    break;
                case "RECRUITER":
                    response.sendRedirect("/filterCandidates");
                    break;
                case "MANAGER":
                    response.sendRedirect("/ApproveReject/offers");
                    break;
                case "INTERVIEWER":
                    response.sendRedirect("/interviewschedules/detail");
                    break;
                default:
                    response.sendRedirect("/default");
                    break;
            }
        };
    }
    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.sendRedirect("/login?error=true");
        };
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }
}
