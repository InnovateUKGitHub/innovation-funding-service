package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.threads.security.QueryPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.innovateuk.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QueryPermissionRulesTest extends BasePermissionRulesTest<QueryPermissionRules> {
    private QueryResource queryResource;
    private UserResource projectFinanceUser;
    private UserResource financeContactUser;

    @Before
    public void setUp() throws Exception {
        projectFinanceUser = projectFinanceUser();
        financeContactUser = getUserWithRole(FINANCE_CONTACT);
        queryResource = new QueryResource(1L, 22L, PROJECT_FINANCE.getClass().getName(), new ArrayList<>(),
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
        assertTrue(rules.onlyInternalOrProjectFinanceUsersCanAddPosts(queryResource, projectFinanceUser));
        assertFalse(rules.onlyInternalOrProjectFinanceUsersCanAddPosts(queryResource, financeContactUser));
    }

    @Test
    public void testThatOnlyTheCorrectFinanceContactCanAddPostsToAQuery() throws Exception {
        queryResource.posts.add(new PostResource(1L, projectFinanceUser, "The body", new ArrayList<>(), LocalDateTime.now()));
        //mock getting the project finance organisation with the projectFinanceUser
        assertTrue(rules.onlyInternalOrProjectFinanceUsersCanAddPosts(queryResource, projectFinanceUser));
    }

    @Test
    public void testThatOnlyInternalProjectFinanceUsersCanDeleteQueries() throws Exception {
        assertTrue(rules.onlyInternalUsersCanDeleteQueries(queryResource, projectFinanceUser));
        assertFalse(rules.onlyInternalUsersCanDeleteQueries(queryResource, financeContactUser));
    }

    @Test
    public void testThatOnlyInternalUsersOrFinanceContactCanViewTheirQueries() {
        assertTrue(rules.onlyInternalUsersOrFinanceContactCanViewTheirQueries(queryResource, projectFinanceUser));
        //mock getting the project finance organisation with the projectFinanceUser
        assertTrue(rules.onlyInternalUsersOrFinanceContactCanViewTheirQueries(queryResource, financeContactUser));
    }

}
