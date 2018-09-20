package org.innovateuk.ifs.eugrant.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.eugrant.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuFundingResourceBuilder extends BaseBuilder<EuFundingResource, EuFundingResourceBuilder> {

    private EuFundingResourceBuilder(List<BiConsumer<Integer, EuFundingResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static EuFundingResourceBuilder newEuFundingResource() {
        return new EuFundingResourceBuilder(emptyList());
    }

    @Override
    protected EuFundingResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuFundingResource>> actions) {
        return new EuFundingResourceBuilder(actions);
    }

    @Override
    protected EuFundingResource createInitial() {
        return new EuFundingResource();
    }

    public EuFundingResourceBuilder withGrantAgreementNumber(String... grantAgreementNumbers) {
        return withArray((grantAgreementNumber, funding) -> funding.setGrantAgreementNumber(grantAgreementNumber), grantAgreementNumbers);
    }

    public EuFundingResourceBuilder withParticipantId(String... participantIds) {
        return withArray((participantId, funding) -> funding.setParticipantId(participantId), participantIds);
    }

    public EuFundingResourceBuilder withProjectName(String... projectNames) {
        return withArray((projectName, funding) -> funding.setProjectName(projectName), projectNames);
    }

    public EuFundingResourceBuilder withProjectStartDate(LocalDate... projectStartDates) {
        return withArray((projectStartDate, funding) -> funding.setProjectStartDate(projectStartDate), projectStartDates);
    }

    public EuFundingResourceBuilder withProjectEndDate(LocalDate... projectEndDates) {
        return withArray((projectEndDate, funding) -> funding.setProjectEndDate(projectEndDate), projectEndDates);
    }

    public EuFundingResourceBuilder withFundingContribution(BigDecimal... fundingContributions) {
        return withArray((fundingContribution, funding) -> funding.setFundingContribution(fundingContribution), fundingContributions);
    }

    public EuFundingResourceBuilder withProjectCoordinator(Boolean... projectCoordinators) {
        return withArray((projectCoordinator, funding) -> funding.setProjectCoordinator(projectCoordinator), projectCoordinators);
    }

    public EuFundingResourceBuilder withActionType(EuActionTypeResource... actionTypes) {
        return withArray((actionType, funding) -> funding.setActionType(actionType), actionTypes);
    }


}