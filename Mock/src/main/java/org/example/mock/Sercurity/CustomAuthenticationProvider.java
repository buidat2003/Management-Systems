    package org.example.mock.Sercurity;

    import org.example.mock.Model.User;
    import org.example.mock.Repository.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Component;

    import java.util.Collections;
    import java.util.Optional;

    @Component
    public class CustomAuthenticationProvider implements AuthenticationProvider {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;  // Inject PasswordEncoder

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();

            // Tìm người dùng trong cơ sở dữ liệu
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = userOpt.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return new UsernamePasswordAuthenticationToken(
                        user, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );
            } else {
                throw new RuntimeException("Invalid password");
            }
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
        }
    }
