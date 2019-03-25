package org.innovateuk.ifs.application.populator.granttransfer;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.populator.researchCategory.AbstractLeadOnlyModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantAgreementSummaryViewModel;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantTransferDetailsSummaryViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GrantTransferSummaryPopulator extends AbstractLeadOnlyModelPopulator {

    private final EuGrantTransferRestService grantTransferRestService;

    public GrantTransferSummaryPopulator(ApplicantRestService applicantRestService, QuestionRestService questionRestService, EuGrantTransferRestService grantTransferRestService) {
        super(applicantRestService, questionRestService);
        this.grantTransferRestService = grantTransferRestService;
    }

    public GrantTransferDetailsSummaryViewModel populateDetails(ApplicationResource application,
                                                                long loggedInUserId,
                                                                boolean userIsLeadApplicant) {
        Optional<EuGrantTransferResource> grantTransferResource = grantTransferRestService.findDetailsByApplicationId(application.getId()).getOptionalSuccessObject();

        QuestionResource question = questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(application.getCompetition(), QuestionSetupType.GRANT_TRANSFER_DETAILS).getSuccess();

        boolean isComplete = isComplete(application, loggedInUserId, QuestionSetupType.GRANT_TRANSFER_DETAILS);
        boolean allReadOnly = !userIsLeadApplicant || isComplete;

        if (grantTransferResource.isPresent()) {
            return new GrantTransferDetailsSummaryViewModel(
                    question.getId(),
                    application.getId(),
                    isApplicationSubmitted(application) || !isCompetitionOpen(application),
                    isComplete,
                    userIsLeadApplicant,
                    allReadOnly,
                    grantTransferResource.get().getGrantAgreementNumber(),
                    grantTransferResource.get().getParticipantId(),
                    grantTransferResource.get().getProjectName(),
                    grantTransferResource.get().getProjectStartDate(),
                    grantTransferResource.get().getProjectEndDate(),
                    grantTransferResource.get().getFundingContribution(),
                    grantTransferResource.get().getProjectCoordinator(),
                    grantTransferResource
                            .map(EuGrantTransferResource::getActionType)
                            .orElse(null)
            );
        }
        return new GrantTransferDetailsSummaryViewModel(
                question.getId(),
                application.getId(),
                isApplicationSubmitted(application) || !isCompetitionOpen(application),
                isComplete,
                userIsLeadApplicant,
                allReadOnly);
    }

    public GrantAgreementSummaryViewModel populateAgreement(ApplicationResource application,
                                                            long loggedInUserId,
                                                            boolean userIsLeadApplicant) {
        QuestionResource question = questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(application.getCompetition(), QuestionSetupType.GRANT_AGREEMENT).getSuccess();

        boolean isComplete = isComplete(application, loggedInUserId, QuestionSetupType.GRANT_AGREEMENT);
        boolean allReadOnly = !userIsLeadApplicant || isComplete;


        Optional<FileEntryResource> grantAgreement = grantTransferRestService.findGrantAgreement(application.getId()).getOptionalSuccessObject();
        return new GrantAgreementSummaryViewModel(
                question.getId(),
                application.getId(),
                isApplicationSubmitted(application) || !isCompetitionOpen(application),
                isComplete,
                userIsLeadApplicant,
                allReadOnly,
                grantAgreement.map(FileEntryResource::getName).orElse(null));
    }
}
