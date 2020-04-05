package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectApplicationPermissionRulesTest extends BasePermissionRulesTest<ProjectApplicationPermissionRules> {

    @Override
    protected ProjectApplicationPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectApplicationPermissionRules();
    }

    @Test
    public void projectPartnerCanViewApplicationsLinkedToTheirProjects() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepository.findOneByApplicationId(application.getId())).thenReturn(linkedProject);
        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(linkedProject.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).
                thenReturn(newProjectUser().build(1));

        assertTrue(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepository).findOneByApplicationId(application.getId());
        verify(projectUserRepository).findByProjectIdAndUserIdAndRoleIsIn(linkedProject.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()));
    }

    @Test
    public void projectPartnerCanViewApplicationsLinkedToTheirProjectsButNoProjectForApplication() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepository.findOneByApplicationId(application.getId())).thenReturn(null);

        assertFalse(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepository).findOneByApplicationId(application.getId());
        verify(projectUserRepository, never()).findByProjectIdAndUserIdAndRole(linkedProject.getId(), user.getId(), PROJECT_PARTNER);
    }

    @Test
    public void projectPartnerCanViewApplicationsLinkedToTheirProjectsButNotPartnerOnLinkedProject() {

        UserResource user = newUserResource().build();
        ApplicationResource application = newApplicationResource().build();
        Project linkedProject = newProject().build();

        when(projectRepository.findOneByApplicationId(application.getId())).thenReturn(linkedProject);
        when(projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(linkedProject.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).
                thenReturn(emptyList());

        assertFalse(rules.projectPartnerCanViewApplicationsLinkedToTheirProjects(application, user));

        verify(projectRepository).findOneByApplicationId(application.getId());
        verify(projectUserRepository).findByProjectIdAndUserIdAndRoleIsIn(linkedProject.getId(), user.getId(), PROJECT_USER_ROLES.stream().collect(Collectors.toList()));
    }


}
