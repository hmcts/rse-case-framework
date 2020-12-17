package uk.gov.hmcts.ccf;

import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.jooq.generated.Tables.USERS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnProperty(value = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DefaultDSLContext jooq;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests(x -> x.anyRequest().authenticated())
                .oauth2Login(x -> x.successHandler(this.loginHandler))
                .logout(l -> l.logoutSuccessHandler(oidcLogoutSuccessHandler()))
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/web/**"));
    }

    AuthenticationSuccessHandler loginHandler = new SavedRequestAwareAuthenticationSuccessHandler() {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws ServletException, IOException {
            OidcUser user = (OidcUser) authentication.getPrincipal();
            jooq.insertInto(USERS)
                .columns(USERS.USER_ID, USERS.USER_FORENAME, USERS.USER_SURNAME)
                .values(user.getSubject(), user.getGivenName(), user.getFamilyName())
                .onDuplicateKeyUpdate()
                .set(USERS.USER_FORENAME, user.getGivenName())
                .set(USERS.USER_SURNAME, user.getFamilyName())
                .execute();
            response.sendRedirect("/");
        }
    };

    /**
     Map IDAM roles to spring Authorities
     for use in access control.
     */
    @Bean
    public GrantedAuthoritiesMapper mapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (OidcUserAuthority.class.isInstance(authority)) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority)authority;

                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                    // Map the roles IDAM returns from /userinfo
                    // to spring security authorities.
                    Object roles = userInfo.getClaims().get("roles");
                    if (roles instanceof ArrayList) {
                        ArrayList a = (ArrayList) roles;
                        for (Object o : a) {
                            mappedAuthorities.add((GrantedAuthority) () -> o.toString());
                        }
                    }
                }
            });

            return mappedAuthorities;
        };
    }

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(
                        this.clientRegistrationRepository);

        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("http://localhost:4200");

        return oidcLogoutSuccessHandler;
    }
}
