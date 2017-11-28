package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategoryType;
import org.innovateuk.ifs.project.financechecks.domain.CostGroup;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;

import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class SpendProfileBuilder extends BaseBuilder<SpendProfile, SpendProfileBuilder> {

    private SpendProfileBuilder(List<BiConsumer<Integer, SpendProfile>> multiActions) {
        super(multiActions);
    }

    public static SpendProfileBuilder newSpendProfile() {
        return new SpendProfileBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected SpendProfileBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SpendProfile>> actions) {
        return new SpendProfileBuilder(actions);
    }

    @Override
    protected SpendProfile createInitial() {
        return new SpendProfile();
    }

    public SpendProfileBuilder withId(Long... ids) {
        return withArray((id, spendProfile) -> setField("id", id, spendProfile), ids);
    }

    public SpendProfileBuilder withOrganisation(Organisation... organisations) {
        return withArray((organisation, spendProfile) -> setField("organisation", organisation, spendProfile), organisations);
    }

    public SpendProfileBuilder withGeneratedBy(User... users) {
        return withArray((user, spendProfile) -> setField("generatedBy", user, spendProfile), users);
    }

    public SpendProfileBuilder withGeneratedDate(Calendar... dates) {
        return withArray((date, spendProfile) -> setField("generatedDate", date, spendProfile), dates);
    }

    public SpendProfileBuilder withMarkedComplete(Boolean... completed) {
        return withArray((complete, spendProfile) -> setField("markedAsComplete", complete, spendProfile), completed);
    }

    public SpendProfileBuilder withProject(Project... projects) {
        return withArray((project, spendProfile) -> setField("project", project, spendProfile), projects);
    }

    public SpendProfileBuilder withCostCategoryType(CostCategoryType... costCategoryTypes) {
        return withArray((costCategoryType, spendProfile) -> setField("costCategoryType", costCategoryType, spendProfile), costCategoryTypes);
    }

    public SpendProfileBuilder withEligibleCostsGroup(List<Cost>... eligibleCostsGroups) {
        return withArray((eligibleCostsGroup, spendProfile) -> setField("eligibleCosts", new CostGroup("Eligible costs for Partner Organisation", eligibleCostsGroup), spendProfile), eligibleCostsGroups);
    }

    public SpendProfileBuilder withSpendProfileFigures(List<Cost>... spendProfileFigures) {
        return withArray((spendProfileFigure, spendProfile) -> setField("spendProfileFigures", new CostGroup("Spend Profile figures for Partner Organisation", spendProfileFigure), spendProfile), spendProfileFigures);
    }
}
