package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.threads.security.ProjectFinanceQueryPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.innovateuk.ifs.thread.security.ProjectFinanceThreadsTestData.projectFinanceWithUserAsFinanceContact;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectFinanceQueryPermissionRulesTest extends BasePermissionRulesTest<ProjectFinanceQueryPermissionRules> {
    private QueryResource queryResource;
    private UserResource projectFinanceUser;
    private UserResource partner;
    private UserResource incorrectPartner;

    @Before
    public void setUp() throws Exception {
        projectFinanceUser = projectFinanceUser();
        partner = getUserWithRole(PARTNER);

        queryResource = queryWithoutPosts();
        queryResource.posts.add(new PostResource(1L, projectFinanceUser, "The body", new ArrayList<>(), ZonedDateTime.now()));

        incorrectPartner = newUserResource().withId(1993L).withRolesGlobal(newRoleResource()
                .withType(PARTNER).build(1)).build();
        incorrectPartner.setId(1993L);
    }

    private QueryResource queryWithoutPosts() {
        return new QueryResource(1L, 22L, new ArrayList<>(),
                FinanceChecksSectionType.VIABILITY, "First Query", true, ZonedDateTime.now());
    }

    @Override
    protected ProjectFinanceQueryPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinanceQueryPermissionRules();
    }

    @Test
    public void testThatOnlyProjectFinanceProjectFinanceUsersCanCreateQueries() throws Exception {
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, partner));
    }

    @Test
    public void testThatNewQueryMustContainInitialPost() throws Exception {
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryWithoutPosts(), partner));
    }

    @Test
    public void testThatNewQueryInitialPostAuthorMustBeTheCurrentUser() throws Exception {
        assertTrue(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, projectFinanceUser));
        UserResource anotherProjectFinanceUser = newUserResource().withId(675L)
                .withRolesGlobal(newRoleResource().withType(PROJECT_FINANCE).build(1)).build();
        assertFalse(rules.onlyProjectFinanceUsersCanCreateQueries(queryResource, anotherProjectFinanceUser));
    }

    @Test
    public void testThatFirstPostMustComeFromTheProjectFinanceUser() throws Exception {
        QueryResource queryWithoutPosts = queryWithoutPosts();
        assertTrue(rules.projectFinanceUsersCanAddPostToTheirQueries(queryWithoutPosts, projectFinanceUser));
        when(projectFinanceRepositoryMock.findOne(queryWithoutPosts.contextClassPk))
                .thenReturn(projectFinanceWithUserAsFinanceContact(partner));
        assertFalse(rules.projectFinanceUsersCanAddPostToTheirQueries(queryWithoutPosts, partner));
    }

    @Test
    public void testThatOnlyTheProjectFinanceUserOrTheCorrectFinanceContactCanReplyToAQuery() throws Exception {
        when(projectFinanceRepositoryMock.findOne(queryResource.contextClassPk))
                .thenReturn(projectFinanceWithUserAsFinanceContact(partner));
        assertTrue(rules.projectFinanceUsersCanAddPostToTheirQueries(queryResource, projectFinanceUser));
        assertTrue(rules.projectPartnersCanAddPostToTheirQueries(queryResource, partner));
        assertFalse(rules.projectPartnersCanAddPostToTheirQueries(queryResource, incorrectPartner));
    }

    @Test
    public void testThatOnlyProjectFinanceUsersOrProjectUsersCanViewTheirQueries() {
        assertTrue(rules.projectFinanceUsersCanViewQueries(queryResource, projectFinanceUser));
        when(projectFinanceRepositoryMock.findOne(queryResource.contextClassPk))
                .thenReturn(projectFinanceWithUserAsFinanceContact(partner));
        assertTrue(rules.projectPartnersCanViewQueries(queryResource, partner));
        assertFalse(rules.projectPartnersCanViewQueries(queryResource, incorrectPartner));
    }
}
