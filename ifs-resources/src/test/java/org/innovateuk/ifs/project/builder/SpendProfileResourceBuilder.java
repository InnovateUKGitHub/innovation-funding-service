package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.finance.resource.CostGroupResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class SpendProfileResourceBuilder extends BaseBuilder<SpendProfileResource, SpendProfileResourceBuilder> {


    private SpendProfileResourceBuilder(List<BiConsumer<Integer, SpendProfileResource>> multiActions) {
        super(multiActions);
    }

    public static SpendProfileResourceBuilder newSpendProfileResource() {
        return new SpendProfileResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected SpendProfileResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, SpendProfileResource>> actions) {
        return new SpendProfileResourceBuilder(actions);
    }

    @Override
    protected SpendProfileResource createInitial() {
        return new SpendProfileResource();
    }


    public SpendProfileResourceBuilder withOrganisation(Long organisation){
        return with((spendProfileResource) -> spendProfileResource.setOrganisation(organisation));
    }

    public SpendProfileResourceBuilder withProject(Long project){
        return with((spendProfileResource) -> spendProfileResource.setProject(project));
    }

    public SpendProfileResourceBuilder withCostCategoryType(Long costCategoryType){
        return with((spendProfileResource) -> spendProfileResource.setCostCategoryType(costCategoryType));
    }

    public SpendProfileResourceBuilder withEligibleCosts(CostGroupResource eligibleCosts){
        return with((spendProfileResource) -> spendProfileResource.setEligibleCosts(eligibleCosts));
    }

    public SpendProfileResourceBuilder withSpendProfileFigures(CostGroupResource spendProfileFigures){
        return with((spendProfileResource) -> spendProfileResource.setSpendProfileFigures(spendProfileFigures));
    }

    public SpendProfileResourceBuilder withGeneratedBy(UserResource user){
        return with((spendProfileResource) -> spendProfileResource.setGeneratedBy(user));
    }

    public SpendProfileResourceBuilder withGeneratedDate(Calendar date){
        return with((spendProfileResource) -> spendProfileResource.setGeneratedDate(date));
    }
}
