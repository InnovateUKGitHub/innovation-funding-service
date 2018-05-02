package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;

@Service
@Configurable
public class StatelessAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Value("management.contextPath")
    private String monitoringEndpoint;

    @Value("${logout.url}")
    private String logoutUrl;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        boolean redirected = false;
        if (shouldBeAuthenticated(request)) {
            Authentication authentication = userAuthenticationService.getAuthentication(request, true);

            if (authentication != null) {
                UserResource userResource = userAuthenticationService.getAuthenticatedUser(request);
                if (nullOrInactiveUser(userResource)) {
                    response.sendRedirect(logoutUrl);
                } else {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (shouldRedirectToNewSiteTerms(request, userResource)) {
                        response.sendRedirect("/info/new-terms-and-conditions");
                        redirected = true;
                    }
                }
            }
        }

        if (!redirected) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean shouldBeAuthenticated(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !(
                uri.startsWith(monitoringEndpoint) ||
                        uri.startsWith("/js/") ||
                        uri.startsWith("/css/") ||
                        uri.startsWith("/images/") ||
                        uri.equals("/favicon.ico") ||
                        uri.startsWith("/prototypes") ||
                        uri.startsWith("/error")
        );
    }

    private boolean nullOrInactiveUser(UserResource userResource) {
        return userResource == null || INACTIVE == userResource.getStatus();
    }

    private boolean shouldRedirectToNewSiteTerms(HttpServletRequest request,
                                                 UserResource userResource) {
        return siteTermsCheckNeeded(request, userResource) && !userHasAcceptedLatestSiteTerms(userResource);
    }

    private boolean siteTermsCheckNeeded(HttpServletRequest request,
                                         UserResource userResource) {
        return !isSiteInfoRequest(request) && userHasApplicantRole(userResource);
    }

    private boolean isSiteInfoRequest(HttpServletRequest request) {
        // requests include the site terms and conditions, cookie and contact pages and the new site terms form page.
        return request.getRequestURI().startsWith("/info/");
    }

    private boolean userHasApplicantRole(UserResource userResource) {
        Set<Role> applicationRoles = EnumSet.of(APPLICANT, LEADAPPLICANT, COLLABORATOR);
        applicationRoles.retainAll(userResource.getRoles());
        return !applicationRoles.isEmpty();
    }

    private boolean userHasAcceptedLatestSiteTerms(UserResource userResource) {
        SiteTermsAndConditionsResource latestSiteTermsAndConditions = termsAndConditionsRestService
                .getLatestSiteTermsAndConditions().getSuccess();
        return userResource.getTermsAndConditionsIds().contains(latestSiteTermsAndConditions.getId());
    }
}
