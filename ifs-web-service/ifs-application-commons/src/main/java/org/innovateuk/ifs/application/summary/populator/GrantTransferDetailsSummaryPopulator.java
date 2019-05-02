package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.viewmodel.NewGrantTransferDetailsSummaryViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class GrantTransferDetailsSummaryPopulator implements QuestionSummaryViewModelPopulator<NewGrantTransferDetailsSummaryViewModel> {

    private final EuGrantTransferRestService grantTransferRestService;

    public GrantTransferDetailsSummaryPopulator(EuGrantTransferRestService grantTransferRestService) {
        this.grantTransferRestService = grantTransferRestService;
    }

    @Override
    public NewGrantTransferDetailsSummaryViewModel populate(QuestionResource question, ApplicationSummaryData data) {
        Optional<EuGrantTransferResource> grantTransferResource = grantTransferRestService.findDetailsByApplicationId(data.getApplication().getId()).getOptionalSuccessObject();
        if (grantTransferResource.isPresent()) {
            return new NewGrantTransferDetailsSummaryViewModel(
                    data,
                    question,
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
        return new NewGrantTransferDetailsSummaryViewModel(data, question);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.GRANT_TRANSFER_DETAILS);
    }

}
