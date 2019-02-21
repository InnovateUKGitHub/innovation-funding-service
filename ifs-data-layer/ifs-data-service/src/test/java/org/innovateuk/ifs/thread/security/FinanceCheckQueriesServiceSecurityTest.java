package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesService;
import org.innovateuk.ifs.project.queries.transactional.FinanceCheckQueriesServiceImpl;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.threads.security.ProjectFinanceQueryPermissionRules;
import org.innovateuk.ifs.threads.security.QueryLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class FinanceCheckQueriesServiceSecurityTest extends BaseServiceSecurityTest<FinanceCheckQueriesService> {

    private ProjectFinanceQueryPermissionRules queryRules;
    private QueryLookupStrategy queryLookupStrategy;

    @Override
    protected Class<? extends FinanceCheckQueriesService> getClassUnderTest() {
        return FinanceCheckQueriesServiceImpl.class;
    }

    @Before
    public void lookupPermissionRules() {
        queryRules = getMockPermissionRulesBean(ProjectFinanceQueryPermissionRules.class);
        queryLookupStrategy = getMockPermissionEntityLookupStrategiesBean(QueryLookupStrategy.class);
    }

    @Test
    public void test_create() throws Exception {
        final QueryResource queryResource = new QueryResource(null, null, null, null, null, false, null, null, null);
        assertAccessDenied(
                () -> classUnderTest.create(queryResource),
                () -> {
                    verify(queryRules).onlyProjectFinanceUsersCanCreateQueries(isA(QueryResource.class), isA(UserResource.class));
                    verifyNoMoreInteractions(queryRules);
                });
    }

    @Test
    public void test_findOne() throws Exception {
        setLoggedInUser(null);

        when(classUnderTestMock.findOne(1L))
                .thenReturn(serviceSuccess(new QueryResource(1L, null, null, null, null, false, null, null, null)));

        assertAccessDenied(() -> classUnderTest.findOne(1L), () -> {
            verify(queryRules).projectFinanceUsersCanViewQueries(isA(QueryResource.class), isNull(UserResource.class));
            verify(queryRules).projectPartnersCanViewQueries(isA(QueryResource.class), isNull(UserResource.class));

            verifyNoMoreInteractions(queryRules);
        });
    }

    @Test
    public void test_findAll() throws Exception {
        setLoggedInUser(null);

        when(classUnderTestMock.findAll(22L))
                .thenReturn(serviceSuccess(new ArrayList<>(asList(
                        new QueryResource(2L, null, null, null, null, false, null, null, null),
                        new QueryResource(3L, null, null, null, null, false, null, null, null)
                ))));

        ServiceResult<List<QueryResource>> results = classUnderTest.findAll(22L);
        assertEquals(0, results.getSuccess().size());

        verify(queryRules, times(2)).projectFinanceUsersCanViewQueries(isA(QueryResource.class), isNull(UserResource.class));
        verify(queryRules, times(2)).projectPartnersCanViewQueries(isA(QueryResource.class), isNull(UserResource.class));

        verifyNoMoreInteractions(queryRules);
    }

    @Test
    public void test_addPost() throws Exception {
        setLoggedInUser(null);
        when(queryLookupStrategy.findById(3L)).thenReturn(new QueryResource(3L, null, new ArrayList<PostResource>(),
                null, null, false, null, null, null));


        assertAccessDenied(() -> classUnderTest.addPost(isA(PostResource.class), 3L), () -> {
            verify(queryRules).projectFinanceUsersCanAddPostToTheirQueries(isA(QueryResource.class), isNull(UserResource.class));
            verify(queryRules).projectPartnersCanAddPostToTheirQueries(isA(QueryResource.class), isNull(UserResource.class));

            verifyNoMoreInteractions(queryRules);
        });
    }
}