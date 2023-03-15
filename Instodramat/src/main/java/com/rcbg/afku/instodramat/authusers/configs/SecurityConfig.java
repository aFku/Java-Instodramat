package com.rcbg.afku.instodramat.authusers.configs;

import com.rcbg.afku.instodramat.authusers.filters.ExceptionPropagatorFilter;
import com.rcbg.afku.instodramat.authusers.filters.UserProfileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer().jwt();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
        http.authorizeHttpRequests().requestMatchers("/api/v1/profile/create").permitAll();
        http.authorizeHttpRequests().requestMatchers("/api/v1/**").authenticated();
        http.authorizeHttpRequests().requestMatchers("/v3/api-docs").permitAll();
        http.authorizeHttpRequests().requestMatchers("/v3/api-docs.yaml").permitAll();
        http.authorizeHttpRequests().requestMatchers("/v3/api-docs/**").permitAll();
        http.authorizeHttpRequests().requestMatchers("/swagger-ui/**").permitAll();
        http.authorizeHttpRequests().requestMatchers("/images/*").authenticated();
        http.authorizeHttpRequests().anyRequest().permitAll();
        return http.build();
    }
}
