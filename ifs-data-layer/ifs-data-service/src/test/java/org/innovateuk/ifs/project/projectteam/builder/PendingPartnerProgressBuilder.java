package org.innovateuk.ifs.project.projectteam.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.CompanyAge;
import org.innovateuk.ifs.application.resource.CompanyPrimaryFocus;
import org.innovateuk.ifs.application.resource.CompetitionReferralSource;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.project.builder.PendingPartnerProgressResourceBuilder;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.user.domain.ProcessRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

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

    public PendingPartnerProgressBuilder withCompletedOn(ZonedDateTime... completedOn){
        return withArraySetFieldByReflection("completedOn", completedOn);
    }


}
