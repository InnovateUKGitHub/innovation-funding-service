package com.worth.ifs.application.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;

import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class SectionRules {

    @PermissionRule(value = "READ", description = "user can read section")
    public boolean userCanReadSection(SectionResource section, User user){
        return SecurityRuleUtil.isCompAdmin(user) || userIsConnectedToSection(section, user);
    }

    @PermissionRule(value = "UPDATE", description ="no one can update sections yet")
    public boolean userCanUpdateSection(SectionResource section, User user){
        return false;
    }

    private boolean userIsConnectedToSection(SectionResource sectionResource, User user){
        return user.getProcessRoles().stream()
                .map(ProcessRole::getApplication)
                .map(Application::getCompetition)
                .map(Competition::getId)
                .anyMatch(competitionId -> sectionResource.getCompetition().equals(competitionId));
    }
}
