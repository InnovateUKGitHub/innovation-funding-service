package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class AbstractIfsCrudServiceImplTest {

    private TestService service = new TestService();

    @Mock
    private TestRepository repository;

    @Mock
    private TestMapper testMapper;

    @Before
    public void before() {
        setField(service, "mapper", testMapper);
    }

    @Test
    public void get() {
        long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.of(new TestDomain()));

        ServiceResult<TestResource> result = service.get(id);

        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void getByIds() {
        List<Long> ids = newArrayList(1L, 2L, 3L);

        when(repository.findAllById(ids)).thenReturn(newArrayList(new TestDomain()));

        ServiceResult<List<TestResource>> result = service.get(ids);

        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void update() {
        long id = 1L;
        TestResource resource = new TestResource();
        when(repository.findById(id)).thenReturn(Optional.of(new TestDomain()));

        ServiceResult<TestResource> result = service.update(id, resource);

        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void delete() {
        long id = 1L;

        ServiceResult<Void> result = service.delete(id);

        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void create() {
        TestResource resource = new TestResource();
        when(repository.save(any())).thenReturn(new TestDomain());

        ServiceResult<TestResource> result = service.create(resource);

        assertThat(result.isSuccess(), is(true));
    }

    private class TestResource {}
    private class TestDomain {}
    private interface TestRepository extends CrudRepository<TestDomain, Long> {}
    private abstract class TestMapper extends BaseResourceMapper<TestDomain, TestResource> {}
    private class TestService extends AbstractIfsCrudServiceImpl<TestResource, TestDomain, Long> {

        @Override
        protected CrudRepository<TestDomain, Long> crudRepository() {
            return repository;
        }

        @Override
        protected Class<TestDomain> getDomainClazz() {
            return TestDomain.class;
        }

        @Override
        protected TestDomain mapToDomain(TestDomain testDomain, TestResource testResource) {
            return testDomain;
        }
    }
}