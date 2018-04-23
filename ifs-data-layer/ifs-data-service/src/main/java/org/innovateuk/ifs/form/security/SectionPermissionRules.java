package org.innovateuk.ifs.form.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class SectionPermissionRules extends BasePermissionRules {
    @PermissionRule(value = "READ", description = "everyone can read sections")
    public boolean userCanReadSection(SectionResource section, UserResource user) {
        return true;
    }

    @PermissionRule(value = "UPDATE", description = "no one can update sections yet")
    public boolean userCanUpdateSection(SectionResource section, UserResource user) {
        return false;
    }

}
