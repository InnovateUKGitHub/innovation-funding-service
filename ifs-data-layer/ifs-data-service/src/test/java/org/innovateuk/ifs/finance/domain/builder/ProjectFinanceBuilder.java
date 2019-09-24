package org.innovateuk.ifs.finance.domain.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.BuilderAmendFunctions.uniqueIds;

/**
 * Builder for ProjectFinance entities.
 */
public class ProjectFinanceBuilder extends BaseBuilder<ProjectFinance, ProjectFinanceBuilder> {

    public ProjectFinanceBuilder withOrganisation(Organisation... value) {
        return withArray((v, finance) -> finance.setOrganisation(v), value);
    }

    public ProjectFinanceBuilder withOrganisationSize(OrganisationSize... value) {
        return withArray((v, finance) -> finance.setOrganisationSize(v), value);
    }

    public ProjectFinanceBuilder withProject(Project... value) {
        return withArray((v, finance) -> finance.setProject(v), value);
    }

    private ProjectFinanceBuilder(List<BiConsumer<Integer, ProjectFinance>> newMultiActions) {
        super(newMultiActions);
    }

    public static ProjectFinanceBuilder newProjectFinance() {
        return new ProjectFinanceBuilder(emptyList()).
                with(uniqueIds());
    }

    @Override
    protected ProjectFinanceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectFinance>> actions) {
        return new ProjectFinanceBuilder(actions);
    }

    @Override
    protected ProjectFinance createInitial() {
        return new ProjectFinance();
    }
}
