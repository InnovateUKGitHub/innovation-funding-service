package com.worth.ifs.organisation.security;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.user.domain.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.flattenLists;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Permission Rules determining who can perform which actions upon an Organisation
 */
@Component
@PermissionRules
public class OrganisationRules {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "READ", description = "A member of an Organisation can view their own Organisation")
    public boolean memberOfOrganisationCanViewOwnOrganisation(OrganisationResource organisation, UserResource user) {
        return organisation.getUsers().contains(user.getId());
    }

    @PermissionRule(value = "READ", description = "Users linked to Applications can view the basic details of the other Organisations on their own Applications")
    public boolean usersCanViewOrganisationsOnTheirOwnApplications(OrganisationResource organisation, UserResource user) {

        // TODO DW - INFUND-1556 - this code feels pretty heavy given that all we need to do is find a link between a User and an Organisation via an Application
        Iterable<ProcessRole> applicationRoles =  processRoleRepository.findAll(user.getProcessRoles());
        List<Application> applicationsThatThisUserIsLinkedTo = new ArrayList<>();
        applicationRoles.forEach(ar -> applicationsThatThisUserIsLinkedTo.add(ar.getApplication()));
        List<ProcessRole> processRolesForAllApplications = flattenLists(simpleMap(applicationsThatThisUserIsLinkedTo, Application::getProcessRoles));
        Set<ProcessRole> uniqueProcessRoles = new HashSet<>(processRolesForAllApplications);
        Set<Organisation> uniqueOrganisations = new HashSet<>(simpleMap(uniqueProcessRoles, ProcessRole::getOrganisation));

        return simpleMap(uniqueOrganisations, Organisation::getId).contains(organisation.getId());
    }
}
