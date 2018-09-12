package org.innovateuk.ifs.eugrant.domain;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.euactiontype.domain.EuActionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuFundingBuilder extends BaseBuilder<EuFunding, EuFundingBuilder> {

    private EuFundingBuilder(List<BiConsumer<Integer, EuFunding>> multiActions) {
        super(multiActions);
    }

    public static EuFundingBuilder newEuFunding() {
        return new EuFundingBuilder(emptyList());
    }

    @Override
    protected EuFundingBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuFunding>> actions) {
        return new EuFundingBuilder(actions);
    }

    @Override
    protected EuFunding createInitial() {
        return new EuFunding();
    }

    public EuFundingBuilder withGrantAgreementNumber(String... grantAgreementNumbers) {
        return withArray((grantAgreementNumber, funding) -> funding.setGrantAgreementNumber(grantAgreementNumber), grantAgreementNumbers);
    }

    public EuFundingBuilder withParticipantId(String... participantIds) {
        return withArray((participantId, funding) -> funding.setParticipantId(participantId), participantIds);
    }

    public EuFundingBuilder withProjectName(String... projectNames) {
        return withArray((projectName, funding) -> funding.setProjectName(projectName), projectNames);
    }

    public EuFundingBuilder withProjectStartDate(LocalDate... projectStartDates) {
        return withArray((projectStartDate, funding) -> funding.setProjectStartDate(projectStartDate), projectStartDates);
    }

    public EuFundingBuilder withProjectEndDate(LocalDate... projectEndDates) {
        return withArray((projectEndDate, funding) -> funding.setProjectEndDate(projectEndDate), projectEndDates);
    }

    public EuFundingBuilder withFundingContribution(BigDecimal... fundingContributions) {
        return withArray((fundingContribution, funding) -> funding.setFundingContribution(fundingContribution), fundingContributions);
    }

    public EuFundingBuilder withProjectCoordinator(Boolean... projectCoordinators) {
        return withArray((projectCoordinator, funding) -> funding.setProjectCoordinator(projectCoordinator), projectCoordinators);
    }

    public EuFundingBuilder withActionType(EuActionType... actionTypes) {
        return withArray((actionType, funding) -> funding.setActionType(actionType), actionTypes);
    }
}
