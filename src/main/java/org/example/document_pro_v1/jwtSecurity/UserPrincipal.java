package org.example.document_pro_v1.jwtSecurity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.example.document_pro_v1.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Long tenantId;
    private Collection<? extends GrantedAuthority> authorities;


    public static UserPrincipal create(User user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + user.getRole(). name());

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getTenant().getId(),
                Collections.singletonList(grantedAuthority)
        );
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
