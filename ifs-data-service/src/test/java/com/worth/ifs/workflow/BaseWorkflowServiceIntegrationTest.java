package com.worth.ifs.workflow;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.workflow.repository.ProcessRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
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
public abstract class BaseWorkflowServiceIntegrationTest<WorkflowServiceType, ProcessRepositoryType extends ProcessRepository<?>, BaseActionType> extends BaseIntegrationTest {

    private Map<Class<? extends Repository>, Pair<? extends Repository, ? extends Repository>> mocks = new HashMap<>();

    @Autowired
    private GenericApplicationContext applicationContext;

    @Before
    public void swapOutRepositoriesForActions() {

        List<Class<? extends Repository>> repositoriesToMock = getRepositoriesToMock();

        repositoriesToMock.forEach(repositoryClass -> {
            Repository mock = mock(repositoryClass);
            mocks.put(repositoryClass, Pair.of(mock, null));
        });

        Map<String, BaseActionType> actions = applicationContext.getBeansOfType(getBaseActionType());
        actions.values().forEach(this::setMockRepositoriesOnTarget);

        Map<String, WorkflowServiceType> workflowServiceBean = applicationContext.getBeansOfType(getWorkflowServiceType());
        WorkflowServiceType workflowService = new ArrayList<>(workflowServiceBean.values()).get(0);
        setMockRepositoriesOnTarget(workflowService);

        collectMocks(this::getMock);
    }

    protected abstract void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier);

    private <R extends Repository> R getMock(Class<R> mockClass) {
        return (R) mocks.get(mockClass).getKey();
    }

    private void setMockRepositoriesOnTarget(Object action) {
        mocks.forEach((mockClass, mockAndReal) -> {
            String clazzName = mockClass.getSimpleName();
            String beanName = clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);

            try {
                ReflectionTestUtils.setField(action, beanName, mockAndReal.getKey());
            } catch (IllegalArgumentException e) {
                // the target may not need this mocked out repo - not to worry
            }

            if (mockClass.equals(getProcessRepositoryType())) {
                try {
                    ReflectionTestUtils.setField(action, "processRepository", mockAndReal.getKey());
                } catch (IllegalArgumentException e) {
                    // the target may not need this mocked out repo - not to worry
                }
            }
        });
    }

    protected abstract Class<BaseActionType> getBaseActionType();

    protected abstract Class<WorkflowServiceType> getWorkflowServiceType();

    protected abstract Class<ProcessRepositoryType> getProcessRepositoryType();

    protected abstract List<Class<? extends Repository>> getRepositoriesToMock();
}
