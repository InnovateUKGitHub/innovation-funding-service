package org.innovateuk.ifs.finance.builder;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for ProjectFinance entities.
 */
public class ProjectFinanceResourceBuilder extends BaseFinanceResourceBuilder<ProjectFinanceResource, ProjectFinanceResourceBuilder> {

    public ProjectFinanceResourceBuilder withProject(Long... projectId) {
        return withArray((id, finance) -> finance.setProject(id), projectId);
    }

    private ProjectFinanceResourceBuilder(List<BiConsumer<Integer, ProjectFinanceResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static ProjectFinanceResourceBuilder newProjectFinanceResource() {
        return new ProjectFinanceResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectFinanceResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectFinanceResource>> actions) {
        return new ProjectFinanceResourceBuilder(actions);
    }

    @Override
    protected ProjectFinanceResource createInitial() {
        return new ProjectFinanceResource();
    }
}
