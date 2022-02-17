package org.innovateuk.ifs.starters.stubdev.security;

import com.google.common.collect.ImmutableList;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class StubAuthFilter extends OncePerRequestFilter {

    /**
     * This is an unwrapped spring agnostic version of what the rest of the code uses (UserResource)
     */
    private static final StubAuthentication DEFAULT_USER = new StubAuthentication(
            25L,
            "Stub User",
            ImmutableList.of(
                new SimpleGrantedAuthority("super_admin_user"),
                new SimpleGrantedAuthority("ifs_administrator"),
                new SimpleGrantedAuthority("project_finance"),
                new SimpleGrantedAuthority("comp_admin")
            )
    );

    /**
     * Simply set the 'logged-in' user on the security context.
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(DEFAULT_USER);
        doFilter(request, response, filterChain);
    }
}
