package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.UidSupplier;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.singletonList;

/**
 * TODO DW - document this class
 */
@Component
public class WebUserSecuritySetter {

    @Autowired
    private UidSupplier uidSupplier;

    @Value("${ifs.web.system.user.uid}")
    private String webUserId;

    public static UserResource webUser;

    static {
        webUser = new UserResource();
        webUser.setRoles(singletonList(Role.SYSTEM_REGISTRATION_USER));
        webUser.setEmail("ifs_web_user@innovateuk.org");
        webUser.setFirstName("IFS Web");
        webUser.setLastName("System User");
    }

    public boolean isWebUser(HttpServletRequest request) {
        return webUserId.equals(uidSupplier.getUid(request));
    }

    public void setWebUser() {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(webUser));
    }

    public void clearWebUser() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
