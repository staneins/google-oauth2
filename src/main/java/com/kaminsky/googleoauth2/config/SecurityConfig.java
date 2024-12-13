package com.kaminsky.googleoauth2.config;

import com.kaminsky.googleoauth2.service.SocialAppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SocialAppService socialAppService;

    public SecurityConfig(SocialAppService socialAppService) {
        this.socialAppService = socialAppService;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/error");
    }

    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
        return new SimpleUrlLogoutSuccessHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/login", "/error", "/webjars/**").permitAll()
                        .requestMatchers("/h2-console/*").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(socialAppService))
                        .defaultSuccessUrl("/user")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}
