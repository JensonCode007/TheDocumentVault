package org.example.document_pro_v1.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.document_pro_v1.Enums.Role;
import org.example.document_pro_v1.dto.AuthResponse;
import org.example.document_pro_v1.dto.LoginRequest;
import org.example.document_pro_v1.dto.RegisterRequest;
import org.example.document_pro_v1.entity.Tenant;
import org.example.document_pro_v1.entity.User;
import org.example.document_pro_v1.jwtSecurity.JwtTokenProvider;
import org.example.document_pro_v1.jwtSecurity.UserPrincipal;
import org.example.document_pro_v1.repository.TenantRepository;
import org.example.document_pro_v1.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse signUpRequest(RegisterRequest registerRequest) {

        if(userRepository.existsByEmail(registerRequest.email())){
            log.info("Username {} already exists", registerRequest.email());
            throw new RuntimeException("Email is already registered");
        }
        Tenant tenant = tenantRepository.findBySlug(registerRequest.tenantSlug())
                .orElseThrow(() -> {
                    log.info("Tenant {} not found", registerRequest.tenantSlug());
                    return new RuntimeException("Tenant not found");
                });

        Role role = registerRequest.role();
        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setTenant(tenant);
        user.setRole(role);
        userRepository.save(user);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                registerRequest.email(),
                                registerRequest.password()
                        )
                );

        String token = jwtTokenProvider.generateToken(authentication, tenant.getId(), tenant.getSlug());

        return new AuthResponse(token, user.getEmail(), role, tenant.getId(), tenant.getSlug());

    }

    public AuthResponse loginRequest(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(()-> new RuntimeException("User not found"));
        String tenantId = user.getTenant().getSlug();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        String token =
                jwtTokenProvider.generateToken(
                        authentication,
                        principal.getTenantId(),
                        principal.getTenantSlug()
                );

        return new AuthResponse(
                token,
                principal.getEmail(),
                Role.valueOf(
                        principal.getAuthorities()
                                .iterator()
                                .next()
                                .getAuthority()
                                .replace("ROLE_", "")
                ),
                principal.getTenantId(),
                principal.getTenantSlug()
        );


    }
}
