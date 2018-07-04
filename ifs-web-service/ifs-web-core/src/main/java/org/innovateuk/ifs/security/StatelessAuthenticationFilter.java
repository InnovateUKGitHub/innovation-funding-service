package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Service
@Configurable
public class StatelessAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Autowired
    private CookieUtil cookieUtil;

    @Value("${management.endpoints.web.base-path}")
    private String monitoringEndpoint;

    @Value("${logout.url}")
    private String logoutUrl;

    public static final String SAVED_REQUEST_URL_COOKIE_NAME = "savedRequestUrl";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {


        if (shouldBeAuthenticated(request)) {
            Authentication authentication = userAuthenticationService.getAuthentication(request, true);

            if (authentication != null) {
                UserResource userResource = userAuthenticationService.getAuthenticatedUser(request);
                if (nullOrInactiveUser(userResource)) {
                    response.sendRedirect(logoutUrl);
                    return;
                } else {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (shouldRedirectToNewSiteTerms(request, userResource)) {
                        saveRequestUrl(request, response);
                        response.sendRedirect("/info/new-terms-and-conditions");
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
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
        return isGetRequest(request) &&
                !isSiteInfoRequest(request) &&
                userHasApplicantRole(userResource);
    }

    private boolean isGetRequest(HttpServletRequest request) {
        return GET.name().equals(request.getMethod());
    }

    private boolean isSiteInfoRequest(HttpServletRequest request) {
        // requests include the site terms and conditions, cookie and contact pages and the new site terms form page.
        return request.getRequestURI().startsWith("/info/");
    }

    private boolean userHasApplicantRole(UserResource userResource) {
        return userResource.getRoles().contains(APPLICANT);
    }

    private boolean userHasAcceptedLatestSiteTerms(UserResource userResource) {
        SiteTermsAndConditionsResource latestSiteTermsAndConditions = termsAndConditionsRestService
                .getLatestSiteTermsAndConditions().getSuccess();
        return userResource.getTermsAndConditionsIds().contains(latestSiteTermsAndConditions.getId());
    }

    private void saveRequestUrl(HttpServletRequest request, HttpServletResponse response) {
        // If it wasn't for the stateless design we could use HttpSessionRequestCache in Spring Security to cache the
        // request so that it can be reused after accepting the site terms and conditions. Instead, save the
        // request url in a cookie.
        cookieUtil.saveToCookie(response, SAVED_REQUEST_URL_COOKIE_NAME, UrlUtils.buildFullRequestUrl(
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                request.getRequestURI(),
                request.getQueryString())
        );
    }
}
