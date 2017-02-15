package org.innovateuk.ifs.thread.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesServiceImpl;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectFinanceQueriesServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ProjectFinanceQueriesServiceImpl service;

    @Test
    public void test_findOne() throws Exception {
        Long queryId = 1L;
        Query query = new Query(queryId, null, null, null, null, null);
        QueryResource queryResource = new QueryResource(queryId, null, null, null, null, false, null);
        when(queryRepositoryMock.findOne(queryId)).thenReturn(query);
        when(queryMapper.mapToResource(query)).thenReturn(queryResource);

        QueryResource response = service.findOne(queryId).getSuccessObjectOrThrowException();

        assertEquals(queryResource, response);
    }

    @Test
    public void test_findAll() throws Exception {
        Long contextId = 22L;
        Query query1 = new Query(1L, null, null, null, null, null);
        Query query2 = new Query(2L, null, null, null, null, null);
        List<Query> queries = asList(query1, query2);

        QueryResource queryResource1 = new QueryResource(1L, null, null, null,
                null, false, null);
        QueryResource queryResource2 = new QueryResource(2L, null, null, null,
                null, false, null);
        List<QueryResource> queryResources = asList(queryResource1, queryResource2);

        when(queryRepositoryMock.findAllByClassPkAndClassName(contextId, ProjectFinance.class.getName())).thenReturn(queries);
        when(queryMapper.mapToResource(query1)).thenReturn(queryResource1);
        when(queryMapper.mapToResource(query2)).thenReturn(queryResource2);

        List<QueryResource> response = service.findAll(contextId).getSuccessObjectOrThrowException();

        assertEquals(queryResources, response);
    }


    @Test
    public void test_create() throws Exception {
        QueryResource queryToCreate = new QueryResource(null, 22L, null, null, null, false, null);
        Query queryToCreateAsDomain = new Query(null, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryMapper.mapToDomain(queryToCreate)).thenReturn(queryToCreateAsDomain);

        Query savedQuery = new Query(1L, 22L, ProjectFinance.class.getName(), null, null, null, null);
        when(queryRepositoryMock.save(queryToCreateAsDomain)).thenReturn(savedQuery);

        QueryResource createdQuery = new QueryResource(1L, 22L, null, null, null, false, null);
        when(queryMapper.mapToResource(savedQuery)).thenReturn(createdQuery);


        Long result = service.create(queryToCreate).getSuccessObjectOrThrowException();

        assertEquals(result, Long.valueOf(1L));
    }

    @Test
    public void test_addPost() throws Exception {
        Long queryId = 1L;
        PostResource post = new PostResource(null, newUserResource().withId(33L).build(), null, null, null);
        Post mappedPost = new Post(null, newUser().withId(33L).build(), null, null, null);
        Query targetedQuery = new Query(queryId, null, null, null, null, null, null);
        when(queryRepositoryMock.findOne(queryId)).thenReturn(targetedQuery);

        when(postMapper.mapToDomain(post)).thenReturn(mappedPost);

        assertTrue(service.addPost(post, queryId).isSuccess());
    }


}
