package org.innovateuk.ifs.workflow;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.repository.Repository;
import org.springframework.statemachine.guard.Guard;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Base class for testing workflows that does not require the developer to have to seed the database with test data,
 * but rather swaps out real Repositories for mocks
 */
public abstract class BaseWorkflowHandlerIntegrationTest<WorkflowHandlerType, ProcessRepositoryType extends ProcessRepository<?> & Repository<?, ?>, BaseActionType> extends BaseIntegrationTest {

    private Map<Class<? extends Repository>, Pair<? extends Repository, ? extends Repository>> mocks = new HashMap<>();

    private static Function<Pair<? extends Repository, ? extends Repository>, ? extends Repository> mockSelector = Pair::getKey;

    private static Function<Pair<? extends Repository, ? extends Repository>, ? extends Repository> realSelector = Pair::getValue;

    @Autowired
    private GenericApplicationContext applicationContext;

    @Before
    public void swapOutRepositoriesForActions() {

        List<Class<? extends Repository>> repositoriesToMock = getRepositoriesToMock();

        repositoriesToMock.forEach(repositoryClass -> {
            Repository mock = mock(repositoryClass);
            mocks.put(repositoryClass, Pair.of(mock, applicationContext.getBean(repositoryClass)));
        });

        setRepositoriesOnWorkflowComponents(mockSelector);
        collectMocks(this::getMock);
    }

    @After
    public void replaceSwappedOutRepositories() {
        setRepositoriesOnWorkflowComponents(realSelector);
    }

    protected abstract void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier);

    private <R extends Repository> R getMock(Class<R> mockClass) {
        return (R) mocks.get(mockClass).getKey();
    }

    private void setRepositoriesOnTarget(Object action, Function<Pair<? extends Repository, ? extends Repository>, ? extends Repository> repositorySelector) {

        mocks.forEach((mockClass, mockAndReal) -> {

            String clazzName = mockClass.getSimpleName();
            String beanName = clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);

            try {
                ReflectionTestUtils.setField(action, beanName, repositorySelector.apply(mockAndReal));
            } catch (IllegalArgumentException e) {
                // the target may not need this mocked out repo - not to worry
            }

            if (mockClass.equals(getProcessRepositoryType())) {
                try {
                    ReflectionTestUtils.setField(action, "processRepository", repositorySelector.apply(mockAndReal));
                } catch (IllegalArgumentException e) {
                    // the target may not need this mocked out repo - not to worry
                }
            }
        });
    }

    protected void verifyNoMoreInteractionsWithMocks() {
        verifyNoMoreInteractions(simpleMap(new ArrayList<>(mocks.values()), Pair::getKey).toArray());
    }

    protected abstract Class<BaseActionType> getBaseActionType();

    protected abstract Class<WorkflowHandlerType> getWorkflowHandlerType();

    protected abstract Class<ProcessRepositoryType> getProcessRepositoryType();

    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        return asList(ActivityStateRepository.class, getProcessRepositoryType());
    }

    private void setRepositoriesOnWorkflowComponents(Function<Pair<? extends Repository, ? extends Repository>, ? extends Repository> repositorySelector) {

        Map<String, BaseActionType> actions = applicationContext.getBeansOfType(getBaseActionType());
        actions.values().forEach(a -> setRepositoriesOnTarget(a, repositorySelector));

        Map<String, WorkflowHandlerType> workflowHandlerBean = applicationContext.getBeansOfType(getWorkflowHandlerType());
        WorkflowHandlerType workflowHandler = new ArrayList<>(workflowHandlerBean.values()).get(0);
        setRepositoriesOnTarget(workflowHandler, repositorySelector);

        Map<String, Guard> guards = applicationContext.getBeansOfType(Guard.class);
        guards.values().forEach(g -> setRepositoriesOnTarget(g, repositorySelector));
    }
}
