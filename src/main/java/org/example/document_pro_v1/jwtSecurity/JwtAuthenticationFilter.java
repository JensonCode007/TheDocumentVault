package org.example.document_pro_v1.jwtSecurity;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain chain)throws ServletException, IOException
    {
        try{
            String token = getTokenFromRequest(request);
            if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
                String email = jwtTokenProvider.getUserDetailsFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                String role = jwtTokenProvider.getRoleFromToken(token);

                List<SimpleGrantedAuthority> authorities = Arrays.stream(role.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UsernamePasswordAuthenticationToken authenticationToken =  new UsernamePasswordAuthenticationToken
                        (userDetails
                                , null
                                , authorities
                        );

                authenticationToken.setDetails(Map.of("tenantId", tenantId));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }
        catch (Exception ex){
            log.error("JWT authentication failed: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        chain.doFilter(request, response);

    }
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
