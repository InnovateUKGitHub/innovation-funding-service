package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
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

    @PermissionRule(value = "MARK_SECTION_AS_COMPLETE", description = "Only member of project team can mark a section as complete")
    public boolean onlyMemberOfProjectTeamCanMarkSectionAsComplete(ApplicationResource applicationResource, UserResource user) {
        return isMemberOfProjectTeam(applicationResource.getId(), user);
    }

    @PermissionRule(value = "MARK_SECTION_AS_INCOMPLETE", description = "Only member of project team can mark a section as incomplete")
    public boolean onlyMemberOfProjectTeamCanMarkSectionAsInComplete(ApplicationResource applicationResource, UserResource user) {
        return isMemberOfProjectTeam(applicationResource.getId(), user);
    }

    @PermissionRule(value = "MARK_SECTION_AS_NOT_REQUIRED", description = "Only member of project team can mark a section as not required")
    public boolean onlyMemberOfProjectTeamCanMarkSectionAsNotRequired(ApplicationResource applicationResource, UserResource user) {
        return isMemberOfProjectTeam(applicationResource.getId(), user);
    }
}
