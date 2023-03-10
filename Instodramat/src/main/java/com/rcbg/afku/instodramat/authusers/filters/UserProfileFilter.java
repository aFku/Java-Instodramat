package com.rcbg.afku.instodramat.authusers.filters;

import com.rcbg.afku.instodramat.authusers.domain.ProfileRepository;
import com.rcbg.afku.instodramat.authusers.exceptions.UserNotRegisteredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@NoArgsConstructor
public class UserProfileFilter extends OncePerRequestFilter{

    private ProfileRepository profileRepository;

    JwtDecoder decoder;

    @Autowired
    public UserProfileFilter(@Qualifier("instodramatJwtDecoder") JwtDecoder decoder, ProfileRepository profileRepository){
        this.decoder = decoder;
        this.profileRepository = profileRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        return Boolean.TRUE.equals(request.getRequestURI().equals("/api/v1/profiles/create"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring("Bearer".length()).trim();
            String userId = decoder.decode(jwt).getClaimAsString("sub");
            if (!profileRepository.existsByUserId(userId)) {
                throw new UserNotRegisteredException("You do not have a profile. Please sign up with registration endpoint.");
            }
            filterChain.doFilter(request, response);
        }
    }
}
