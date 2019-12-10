package org.innovateuk.ifs.project.documents.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.SecurityRuleUtil;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentResourceBuilder.newProjectDocumentResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isMonitoringOfficer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class DocumentPermissionRulesTest extends BasePermissionRulesTest<DocumentPermissionRules> {

    @Override
    protected DocumentPermissionRules supplyPermissionRulesUnderTest() {
        return new DocumentPermissionRules();
    }

    @Test
    public void projectManagerCanUploadDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);
        assertTrue(rules.projectManagerCanUploadDocument(project, user));
    }

    @Test
    public void nonProjectManagerCannotUploadDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);
        assertFalse(rules.projectManagerCanUploadDocument(project, user));
    }

    @Test
    public void partnersCanDownloadDocument() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanDownloadDocument(project, user));
    }

    @Test
    public void nonPartnersCannotDownloadDocument() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanDownloadDocument(project, user));
    }

    @Test
    public void internalUserCanDownloadDocument() {
        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUserCanDownloadDocument(project, user));
            } else {
                assertFalse(rules.internalUserCanDownloadDocument(project, user));
            }
        });
    }

    @Test
    public void stakeholderCanDownloadDocument() {
        long projectId = 100L;

        Competition competition = newCompetition()
                .withId(1L)
                .build();

        List<ProjectDocumentResource> projectDocuments = newProjectDocumentResource()
                .withStatus(DocumentStatus.APPROVED)
                .build(1);

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withProjectDocuments(projectDocuments)
                .build();

        Application application = newApplication()
                .withId(1L)
                .withCompetition(competition)
                .withName("Application Name")
                .build();

        Project project = newProject()
                .withId(projectId)
                .withApplication(application)
                .build();

        UserResource user = newUserResource()
                .withRoleGlobal(STAKEHOLDER).build();

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(true);

        assertTrue(rules.stakeholderCanDownloadDocument(projectResource, user));
    }

    @Test
    public void stakeholderCannotDownloadDocument() {
        long projectId = 100L;

        Competition competition = newCompetition()
                .withId(1L)
                .build();

        List<ProjectDocumentResource> projectDocuments = newProjectDocumentResource()
                .withStatus(DocumentStatus.UNSET)
                .build(1);

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withProjectDocuments(projectDocuments)
                .build();

        Application application = newApplication()
                .withId(1L)
                .withCompetition(competition)
                .withName("Application Name")
                .build();

        Project project = newProject()
                .withId(projectId)
                .withApplication(application)
                .build();

        UserResource user = newUserResource()
                .withRoleGlobal(STAKEHOLDER).build();

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(stakeholderRepository.existsByCompetitionIdAndUserId(competition.getId(), user.getId())).thenReturn(false);

        assertFalse(rules.stakeholderCanDownloadDocument(projectResource, user));
    }

    @Test
    public void monitoringOfficerCanDownloadDocument() {
        ProjectResource project = newProjectResource().build();

        setupMonitoringOfficerExpectations(project, monitoringOfficerUser(), true);

        allGlobalRoleUsers.forEach(user -> {
            if (isMonitoringOfficer(user)) {
                assertTrue(rules.monitoringOfficerCanDownloadDocument(project, monitoringOfficerUser()));
            } else {
                assertFalse(rules.monitoringOfficerCanDownloadDocument(project, user));
            }
        });
    }


    @Test
    public void projectManagerCanDeleteDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);
        assertTrue(rules.projectManagerCanDeleteDocument(project, user));
    }

    @Test
    public void nonProjectManagerCannotDeleteDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);
        assertFalse(rules.projectManagerCanDeleteDocument(project, user));
    }

    @Test
    public void projectManagerCanSubmitDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);
        assertTrue(rules.projectManagerCanSubmitDocument(project, user));
    }

    @Test
    public void nonProjectManagerCannotSubmitDocument() {
        ProjectResource project = newProjectResource().build();

        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);
        assertFalse(rules.projectManagerCanSubmitDocument(project, user));
    }

    @Test
    public void internalAdminCanApproveDocument() {
        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (SecurityRuleUtil.isInternalAdmin(user) || SecurityRuleUtil.isIFSAdmin(user)) {
                assertTrue(rules.internalAdminCanApproveDocument(project, user));
            } else {
                assertFalse(rules.internalAdminCanApproveDocument(project, user));
            }
        });
    }
}

