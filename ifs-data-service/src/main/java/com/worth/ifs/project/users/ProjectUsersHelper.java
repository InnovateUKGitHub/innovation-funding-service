package com.worth.ifs.project.users;

import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.mapper.ProjectUserMapper;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

@Component
public class ProjectUsersHelper {
    @Autowired
    ProjectUserRepository projectUserRepository;

    @Autowired
    ProjectUserMapper projectUserMapper;

    @Autowired
    OrganisationRepository organisationRepository;

    public List<ProjectUser> getProjectUsersByProjectId(Long projectId) {
        return projectUserRepository.findByProjectId(projectId);
    }

    public List<Organisation> getPartnerOrganisations(Long projectId) {
        List<ProjectUser> projectUserObjs = getProjectUsersByProjectId(projectId);
        List<ProjectUserResource> projectRoles = simpleMap(projectUserObjs, projectUserMapper::mapToResource);
        return getPartnerOrganisations(projectRoles);
    }

    public List<Organisation> getPartnerOrganisations(List<ProjectUserResource> projectRoles) {
        final Comparator<Organisation> compareById =
                Comparator.comparingLong(Organisation::getId);

        final Supplier<SortedSet<Organisation>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<Organisation> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRoleName().equals(PROJECT_PARTNER.getName()))
                .map(uar -> organisationRepository.findOne(uar.getOrganisation()))
                .collect(Collectors.toCollection(supplier));

        return new ArrayList<>(organisationSet);
    }
}
