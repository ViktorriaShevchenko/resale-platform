package ru.skypro.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.skypro.homework.filter.BasicAuthCorsFilter;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/webjars/**",
            "/login",
            "/register"
    };

    private final DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @Profile("test")
    public UserDetailsManager inMemoryUserDetailsManager() {
        UserDetails user = User.builder()
                .username("user@test.com")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin@test.com")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests(
                        authorization ->
                                authorization
                                        .mvcMatchers(HttpMethod.GET, "/ads").permitAll()
                                        .mvcMatchers(HttpMethod.GET, "/ads/{id}").permitAll()
                                        .mvcMatchers(HttpMethod.GET, "/ads/{id}/comments").permitAll()
                                        .mvcMatchers(AUTH_WHITELIST).permitAll()
                                        .mvcMatchers(HttpMethod.POST, "/ads/**").hasAnyRole("USER", "ADMIN")
                                        .mvcMatchers(HttpMethod.PATCH, "/ads/**").hasAnyRole("USER", "ADMIN")
                                        .mvcMatchers(HttpMethod.DELETE, "/ads/**").hasAnyRole("USER", "ADMIN")
                                        .mvcMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                                        .anyRequest().authenticated())
                .cors()
                .and()
                .httpBasic(withDefaults())
                .addFilterAfter(new BasicAuthCorsFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
