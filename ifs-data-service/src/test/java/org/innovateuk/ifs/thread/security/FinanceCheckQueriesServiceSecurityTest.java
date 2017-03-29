package org.innovateuk.ifs.thread.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.financecheck.service.FinanceCheckQueriesService;
import org.innovateuk.ifs.threads.security.ProjectFinanceQueryPermissionRules;
import org.innovateuk.ifs.threads.security.QueryLookupStrategy;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

public class FinanceCheckQueriesServiceSecurityTest extends BaseServiceSecurityTest<FinanceCheckQueriesService> {

    private ProjectFinanceQueryPermissionRules queryRules;
    private QueryLookupStrategy queryLookupStrategy;

    @Override
    protected Class<? extends FinanceCheckQueriesService> getClassUnderTest() {
        return TestFinanceCheckQueriesService.class;
    }

    @Before
    public void lookupPermissionRules() {
        queryRules = getMockPermissionRulesBean(ProjectFinanceQueryPermissionRules.class);
        queryLookupStrategy = getMockPermissionEntityLookupStrategiesBean(QueryLookupStrategy.class);
    }

    @Test
    public void test_create() throws Exception {
        final QueryResource queryResource = new QueryResource(null, null, null, null, null, false, null);
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

        assertAccessDenied(() -> classUnderTest.findOne(1L), () -> {
            verify(queryRules).projectFinanceUsersCanViewQueries(isA(QueryResource.class), isNull(UserResource.class));
            verify(queryRules).projectPartnersCanViewQueries(isA(QueryResource.class), isNull(UserResource.class));

            verifyNoMoreInteractions(queryRules);
        });
    }

    @Test
    public void test_findAll() throws Exception {
        setLoggedInUser(null);

        ServiceResult<List<QueryResource>> results = classUnderTest.findAll(22L);
        assertEquals(0, results.getSuccessObject().size());

        verify(queryRules, times(2)).projectFinanceUsersCanViewQueries(isA(QueryResource.class), isNull(UserResource.class));
        verify(queryRules, times(2)).projectPartnersCanViewQueries(isA(QueryResource.class), isNull(UserResource.class));

        verifyNoMoreInteractions(queryRules);
    }

    @Test
    public void test_addPost() throws Exception {
        setLoggedInUser(null);
        when(queryLookupStrategy.findById(3L)).thenReturn(new QueryResource(3L, null, new ArrayList<PostResource>(),
                null, null, false, null));


        assertAccessDenied(() -> classUnderTest.addPost(isA(PostResource.class), 3L), () -> {
            verify(queryRules).projectFinanceUsersCanAddPostToTheirQueries(isA(QueryResource.class), isNull(UserResource.class));
            verify(queryRules).projectPartnersCanAddPostToTheirQueries(isA(QueryResource.class), isNull(UserResource.class));

            verifyNoMoreInteractions(queryRules);
        });
    }

    public static class TestFinanceCheckQueriesService implements FinanceCheckQueriesService {

        @Override
        public ServiceResult<List<QueryResource>> findAll(Long contextClassPk) {
            List<QueryResource> queries = new ArrayList<>();
            queries.add(findOne(2L).getSuccessObject());
            queries.add(findOne(3L).getSuccessObject());
            return ServiceResult.serviceSuccess(queries);
        }

        @Override
        public ServiceResult<QueryResource> findOne(Long id) {
            return ServiceResult.serviceSuccess(new QueryResource(id,
                    null, null, null, null, false, null));
        }

        @Override
        public ServiceResult<Long> create(QueryResource QueryResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> addPost(PostResource post, Long queryId) {
            return null;
        }
    }


}