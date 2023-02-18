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
    private final UserProfileFilter userProfileFilter;
    private final ExceptionPropagatorFilter exceptionPropagatorFilter;

    @Autowired
    public SecurityConfig(UserProfileFilter userProfileFilter, ExceptionPropagatorFilter exceptionPropagatorFilter){
        this.userProfileFilter = userProfileFilter;
        this.exceptionPropagatorFilter = exceptionPropagatorFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer().jwt();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
        http.authorizeHttpRequests().requestMatchers("/api/v1/profile/create").permitAll();
        http.authorizeHttpRequests().requestMatchers("/api/v1/**").authenticated().and().addFilterAfter(userProfileFilter, AuthorizationFilter.class);
        http.addFilterBefore(exceptionPropagatorFilter, UserProfileFilter.class);
        return http.build();
    }
}
