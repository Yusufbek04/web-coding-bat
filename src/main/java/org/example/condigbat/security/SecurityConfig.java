package org.example.condigbat.security;

import org.example.condigbat.error.MyEntryPointHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class SecurityConfig {

    private final MyEntryPointHandler myEntryPointHandler = new MyEntryPointHandler();
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        .antMatchers("/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
//                                        .antMatchers("/language/list-for-users")
//                                        .hasAnyRole("USER", "MODERATOR", "ADMIN")
//                                        .antMatchers(
//                                                HttpMethod.POST,
//                                                "/language/add",
//                                                "/language/list",
//                                                "/language/super")
//                                        .hasAnyRole("MODERATOR", "ADMIN")
//                                        .antMatchers(HttpMethod.PUT,
//                                                "/**")
//                                        .hasAnyRole("MODERATOR", "ADMIN")
//                                        .antMatchers("/**")
//                                        .hasRole("ADMIN")

                )
                .httpBasic(withDefaults());
        return http.build();
    }

}
