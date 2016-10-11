package com.worth.ifs.project.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ProjectGrantOfferPermissionRulesTest extends BasePermissionRulesTest<ProjectGrantOfferPermissionRules> {

    @Test
    public void testLeadPartnersCanCreateSignedGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsLeadPartner(project, user);

        assertTrue(rules.leadPartnerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonLeadPartnersCannotCreateSignedGrantOfferLetter() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsLeadPartner(project, user);

        assertFalse(rules.leadPartnerCanUploadGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectManagerCanCreateSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanUploadGrantOfferLetter(project, user));

    }

    @Test
    public void testNonProjectManagerCannotCreateSignedGrantOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerCanUploadGrantOfferLetter(project, user));

    }


    @Test
    public void testPartnersCanViewGrantOfferLetterDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testNonPartnersCannotViewGrantOfferLetterDetails() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanViewGrantOfferLetter(project, user));
    }

    @Test
    public void testPartnersCanDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testNonPartnersCannotDownloadGrantOfferLetterDocuments() {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setupUserNotAsPartner(project, user);

        assertFalse(rules.partnersCanDownloadGrantOfferLetter(project, user));
    }

    @Test
    public void testProjectManagerCanSubmitOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerSubmitGrantOfferLetter(project.getId(), user));

    }

    @Test
    public void testNonProjectManagerCannotSubmitOfferLetter() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerSubmitGrantOfferLetter(project.getId(), user));
    }

    @Override
    protected ProjectGrantOfferPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectGrantOfferPermissionRules();
    }
}