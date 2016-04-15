package com.worth.ifs.application.security;

import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class SectionRules {

    @PermissionRule(value = "READ", description = "everyone can read sections")
    public boolean userCanReadSection(SectionResource section, UserResource user){
        return true;
    }

    @PermissionRule(value = "UPDATE", description ="no one can update sections yet")
    public boolean userCanUpdateSection(SectionResource section, UserResource user){
        return false;
    }

    private boolean userIsConnectedToSection(SectionResource sectionResource, UserResource user){
        /*return user.getProcessRoles().stream()
                .map(ProcessRole::getApplication)
                .map(Application::getCompetition)
                .map(Competition::getId)
                .anyMatch(competitionId -> sectionResource.getCompetition().equals(competitionId));*/
        return false;
    }
}
