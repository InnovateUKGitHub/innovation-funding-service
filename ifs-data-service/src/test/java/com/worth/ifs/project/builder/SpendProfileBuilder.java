package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.user.domain.Organisation;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
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

    public SpendProfileBuilder withApproval(ApprovalType... approvalTypes) {
        return withArray((approvalType, spendProfile) -> setField("approval", approvalType, spendProfile), approvalTypes);
    }
}
