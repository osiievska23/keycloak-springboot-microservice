package com.example.keycloakspringbootmicroservice.rest.security.jwt;

import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ROOT_PATH;
import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.USERS_PATH;
import static com.example.keycloakspringbootmicroservice.constants.ExceptionConstants.CREDENTIALS_INVALID_EXCEPTION;

import com.example.keycloakspringbootmicroservice.rest.repositories.UserRepository;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationUtils jwtAuthenticationUtils;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtAuthenticationUtils jwtAuthenticationUtils, UserRepository userRepository) {
        this.jwtAuthenticationUtils = jwtAuthenticationUtils;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = jwtAuthenticationUtils.getJwtHeaderFromRequest(request);
        if (!jwtAuthenticationUtils.validateToken(jwtToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, CREDENTIALS_INVALID_EXCEPTION);
            return;
        }

//        String userEmailFromJwt = jwtAuthenticationUtils.getUserEmailFromJwt(jwtToken);
//        Optional<User> optionalUser = userRepository.findByEmail(userEmailFromJwt);
//
//        if (optionalUser.isEmpty() || !ACTIVE.equals(optionalUser.get().getStatus())) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, CREDENTIALS_INVALID_EXCEPTION);
//            return;
//        }

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken("userEmailFromJwt", "userEmailFromJwt");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getMethod().equals(HttpMethod.POST.name()) && request.getRequestURI()
            .equals(ROOT_PATH + USERS_PATH);
    }
}
