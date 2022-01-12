package org.innovateuk.ifs.granttransfer.resource;

import org.innovateuk.ifs.BaseBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class EuGrantTransferResourceBuilder extends BaseBuilder<EuGrantTransferResource, EuGrantTransferResourceBuilder> {

    private EuGrantTransferResourceBuilder(List<BiConsumer<Integer, EuGrantTransferResource>> newMultiActions) {
        super(newMultiActions);
    }

    public static EuGrantTransferResourceBuilder newEuGrantTransferResource() {
        return new EuGrantTransferResourceBuilder(emptyList());
    }

    @Override
    protected EuGrantTransferResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuGrantTransferResource>> actions) {
        return new EuGrantTransferResourceBuilder(actions);
    }

    @Override
    protected EuGrantTransferResource createInitial() {
        return new EuGrantTransferResource();
    }

    public EuGrantTransferResourceBuilder withGrantAgreementNumber(String... grantAgreementNumbers) {
        return withArray((grantAgreementNumber, funding) -> funding.setGrantAgreementNumber(grantAgreementNumber), grantAgreementNumbers);
    }

    public EuGrantTransferResourceBuilder withParticipantId(String... participantIds) {
        return withArray((participantId, funding) -> funding.setParticipantId(participantId), participantIds);
    }

    public EuGrantTransferResourceBuilder withProjectName(String... projectNames) {
        return withArray((projectName, funding) -> funding.setProjectName(projectName), projectNames);
    }

    public EuGrantTransferResourceBuilder withProjectStartDate(LocalDate... projectStartDates) {
        return withArray((projectStartDate, funding) -> funding.setProjectStartDate(projectStartDate), projectStartDates);
    }

    public EuGrantTransferResourceBuilder withProjectEndDate(LocalDate... projectEndDates) {
        return withArray((projectEndDate, funding) -> funding.setProjectEndDate(projectEndDate), projectEndDates);
    }

    public EuGrantTransferResourceBuilder withFundingContribution(BigDecimal... fundingContributions) {
        return withArray((fundingContribution, funding) -> funding.setFundingContribution(fundingContribution), fundingContributions);
    }

    public EuGrantTransferResourceBuilder withProjectCoordinator(Boolean... projectCoordinators) {
        return withArray((projectCoordinator, funding) -> funding.setProjectCoordinator(projectCoordinator), projectCoordinators);
    }

    public EuGrantTransferResourceBuilder withActionType(EuActionTypeResource... actionTypes) {
        return withArray((actionType, funding) -> funding.setActionType(actionType), actionTypes);
    }


}