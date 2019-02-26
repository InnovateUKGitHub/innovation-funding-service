package org.innovateuk.ifs.granttransfer.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.granttransfer.domain.EuActionType;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class EuGrantTransferBuilder extends BaseBuilder<EuGrantTransfer, EuGrantTransferBuilder> {

    private EuGrantTransferBuilder(List<BiConsumer<Integer, EuGrantTransfer>> multiActions) {
        super(multiActions);
    }

    public static EuGrantTransferBuilder newEuGrantTransfer() {
        return new EuGrantTransferBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected EuGrantTransferBuilder createNewBuilderWithActions(List<BiConsumer<Integer, EuGrantTransfer>> actions) {
        return new EuGrantTransferBuilder(actions);
    }

    @Override
    protected EuGrantTransfer createInitial() {
        return new EuGrantTransfer();
    }

    public EuGrantTransferBuilder withId(Long... ids) {
        return withArray((id, invite) -> setField("id", id, invite), ids);
    }

    public EuGrantTransferBuilder withGrantAgreementNumber(String... grantAgreementNumbers) {
        return withArray((grantAgreementNumber, funding) -> funding.setGrantAgreementNumber(grantAgreementNumber), grantAgreementNumbers);
    }

    public EuGrantTransferBuilder withParticipantId(String... participantIds) {
        return withArray((participantId, funding) -> funding.setParticipantId(participantId), participantIds);
    }

    public EuGrantTransferBuilder withProjectStartDate(LocalDate... projectStartDates) {
        return withArray((projectStartDate, funding) -> funding.setProjectStartDate(projectStartDate), projectStartDates);
    }

    public EuGrantTransferBuilder withProjectEndDate(LocalDate... projectEndDates) {
        return withArray((projectEndDate, funding) -> funding.setProjectEndDate(projectEndDate), projectEndDates);
    }

    public EuGrantTransferBuilder withFundingContribution(BigDecimal... fundingContributions) {
        return withArray((fundingContribution, funding) -> funding.setFundingContribution(fundingContribution), fundingContributions);
    }

    public EuGrantTransferBuilder withProjectCoordinator(Boolean... projectCoordinators) {
        return withArray((projectCoordinator, funding) -> funding.setProjectCoordinator(projectCoordinator), projectCoordinators);
    }

    public EuGrantTransferBuilder withActionType(EuActionType... actionTypes) {
        return withArray((actionType, funding) -> funding.setActionType(actionType), actionTypes);
    }

    public EuGrantTransferBuilder withGrantAgreement(FileEntry... fileEntries) {
        return withArray((file, funding) -> funding.setGrantAgreement(file), fileEntries);
    }

    public EuGrantTransferBuilder withApplication(Application... applications) {
        return withArray((application, funding) -> funding.setApplication(application), applications);
    }
}