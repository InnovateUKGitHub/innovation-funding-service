package org.innovateuk.ifs.project.projectteam.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class PendingPartnerProgressBuilder extends BaseBuilder<PendingPartnerProgress, PendingPartnerProgressBuilder> {

    private PendingPartnerProgressBuilder(List<BiConsumer<Integer, PendingPartnerProgress>> multiActions) {
        super(multiActions);
    }

    public static PendingPartnerProgressBuilder newPendingPartnerProgress() {
        return new PendingPartnerProgressBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected PendingPartnerProgressBuilder createNewBuilderWithActions(List<BiConsumer<Integer, PendingPartnerProgress>> actions) {
        return new PendingPartnerProgressBuilder(actions);
    }

    @Override
    protected PendingPartnerProgress createInitial() {
        return new PendingPartnerProgress();
    }

    public PendingPartnerProgressBuilder withId(Long... ids) {
        return withArray((id, application) -> setField("id", id, application), ids);
    }

    public PendingPartnerProgressBuilder withYourOrganisationCompletedOn(ZonedDateTime... yourOrganisationCompletedOn){
        return withArraySetFieldByReflection("yourOrganisationCompletedOn", yourOrganisationCompletedOn);
    }

    public PendingPartnerProgressBuilder withYourFundingCompletedOn(ZonedDateTime... yourFundingCompletedOn){
        return withArraySetFieldByReflection("yourFundingCompletedOn", yourFundingCompletedOn);
    }

    public PendingPartnerProgressBuilder withTermsAndConditionsCompletedOn(ZonedDateTime... termsAndConditionsCompletedOn){
        return withArraySetFieldByReflection("yourFundingCompletedOn", termsAndConditionsCompletedOn);
    }

    public PendingPartnerProgressBuilder withPartnerOrganisation(PartnerOrganisation... partnerOrganisations){
        return withArraySetFieldByReflection("partnerOrganisation", partnerOrganisations);
    }

    public PendingPartnerProgressBuilder withSubsidyBasisCompletedOn(ZonedDateTime... subsidyBasisCompletedOn){
        return withArraySetFieldByReflection("subsidyBasisCompletedOn", subsidyBasisCompletedOn);
    }

    public PendingPartnerProgressBuilder withCompletedOn(ZonedDateTime... completedOn){
        return withArraySetFieldByReflection("completedOn", completedOn);
    }


}
