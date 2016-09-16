package com.worth.ifs.workflow;

import com.worth.ifs.BaseIntegrationTest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.Mockito.mock;

/**
 * Base class for testing workflows that does not require the developer to have to seed the database with test data,
 * but rather swaps out real Repositories for mocks
 */
@DirtiesContext
public abstract class BaseWorkflowIntegrationTest<BaseActionType> extends BaseIntegrationTest {

    private Map<Class<? extends Repository>, Pair<? extends Repository, ? extends Repository>> mocks = new HashMap<>();

    @Autowired
    private GenericApplicationContext applicationContext;

    @Before
    public void swapOutRepositories() {

        List<Class<? extends Repository>> repositoriesToMock = getRepositoriesToMock();

        repositoriesToMock.forEach(repositoryClass -> {
            Repository mock = mock(repositoryClass);
            mocks.put(repositoryClass, Pair.of(mock, null));
        });

        Map<String, BaseActionType> actions = applicationContext.getBeansOfType(getBaseActionType());
        actions.values().forEach(this::setMockRepositoriesOnAction);

        collectMocks(this::getMock);
    }

    protected abstract void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier);

    private <R extends Repository> R getMock(Class<R> mockClass) {
        return (R) mocks.get(mockClass).getKey();
    }

    private void setMockRepositoriesOnAction(BaseActionType action) {
        mocks.forEach((mockClass, mockAndReal) -> {
            String clazzName = mockClass.getSimpleName();
            String beanName = clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);
            ReflectionTestUtils.setField(action, beanName, mockAndReal.getKey());
        });
    }

    protected abstract Class<BaseActionType> getBaseActionType();

    protected abstract List<Class<? extends Repository>> getRepositoriesToMock();
}
