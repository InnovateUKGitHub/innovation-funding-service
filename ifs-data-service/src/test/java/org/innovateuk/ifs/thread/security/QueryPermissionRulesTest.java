package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.security.QueryPermissionRules;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class QueryPermissionRulesTest extends BasePermissionRulesTest<QueryPermissionRules> {
    private QueryResource queryResource;
    private UserResource projectFinanceUser;
    private UserResource financeContactUser;
    private UserResource incorrectFinanceContactUser;

    @Before
    public void setUp() throws Exception {
        projectFinanceUser = projectFinanceUser();
        financeContactUser = getUserWithRole(FINANCE_CONTACT);

        queryResource = queryWithoutPosts();
        queryResource.posts.add(new PostResource(1L, projectFinanceUser, "The body", new ArrayList<>(), LocalDateTime.now()));

        incorrectFinanceContactUser = newUserResource().withId(1993L).withRolesGlobal(newRoleResource()
                .withType(FINANCE_CONTACT).build(1)).build();
        incorrectFinanceContactUser.setId(1993L);
    }

    private QueryResource queryWithoutPosts() {
        return new QueryResource(1L, 22L, new ArrayList<>(),
                FinanceChecksSectionType.VIABILITY, "First Query", true, LocalDateTime.now());
    }

    @Override
    protected QueryPermissionRules supplyPermissionRulesUnderTest() {
        return new QueryPermissionRules();
    }

    @Test
    public void testThatOnlyInternalProjectFinanceUsersCanCreateQueries() throws Exception {
        assertTrue(rules.onlyInternalUsersCanCreateQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyInternalUsersCanCreateQueries(queryResource, financeContactUser));
    }

    @Test
    public void testThatFirstPostMustComeFromTheProjectFinanceUser() throws Exception {
        QueryResource queryWithoutPosts = queryWithoutPosts();
        assertTrue(rules.onlyInternalOrProjectFinanceUsersCanAddPosts(queryWithoutPosts, projectFinanceUser));
        when(projectFinanceRepositoryMock.findOne(queryWithoutPosts.contextClassPk))
                .thenReturn(mockedProjectFinanceWithUserAsFinanceContact(financeContactUser));
        assertFalse(rules.onlyInternalOrProjectFinanceUsersCanAddPosts(queryWithoutPosts, financeContactUser));
    }

    @Test
    public void testThatOnlyTheProjectFinanceUserOrTheCorrectFinanceContactCanReplyToAQuery() throws Exception {
        when(projectFinanceRepositoryMock.findOne(queryResource.contextClassPk))
                .thenReturn(mockedProjectFinanceWithUserAsFinanceContact(financeContactUser));
        assertTrue(rules.onlyInternalOrProjectFinanceUsersCanAddPosts(queryResource, projectFinanceUser));
        assertTrue(rules.onlyInternalOrProjectFinanceUsersCanAddPosts(queryResource, financeContactUser));
        assertFalse(rules.onlyInternalOrProjectFinanceUsersCanAddPosts(queryResource, incorrectFinanceContactUser));
    }

    @Test
    public void testThatOnlyInternalProjectFinanceUsersCanDeleteQueries() throws Exception {
        assertTrue(rules.onlyInternalUsersCanDeleteQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyInternalUsersCanDeleteQueries(queryResource, financeContactUser));
    }

    @Test
    public void testThatOnlyInternalUsersOrFinanceContactCanViewTheirQueries() {
        assertTrue(rules.onlyInternalUsersOrFinanceContactCanViewTheirQueries(queryResource, projectFinanceUser));
        when(projectFinanceRepositoryMock.findOne(queryResource.contextClassPk))
                .thenReturn(mockedProjectFinanceWithUserAsFinanceContact(financeContactUser));
        assertTrue(rules.onlyInternalUsersOrFinanceContactCanViewTheirQueries(queryResource, financeContactUser));
        assertFalse(rules.onlyInternalUsersOrFinanceContactCanViewTheirQueries(queryResource, incorrectFinanceContactUser));
    }


    private final ProjectFinance mockedProjectFinanceWithUserAsFinanceContact(UserResource user) {
        Organisation organisation = new Organisation();
        organisation.addUser(newUser().withRoles(newRole().withType(FINANCE_CONTACT).build(1)).withId(financeContactUser.getId()).build());
        return newProjectFinance().withOrganisation(organisation).build();
    }

}
