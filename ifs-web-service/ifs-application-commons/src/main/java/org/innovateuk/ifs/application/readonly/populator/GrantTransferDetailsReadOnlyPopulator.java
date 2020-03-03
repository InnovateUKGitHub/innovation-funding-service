package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.GrantTransferDetailsReadOnlyViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class GrantTransferDetailsReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<GrantTransferDetailsReadOnlyViewModel> {

    private final EuGrantTransferRestService grantTransferRestService;

    public GrantTransferDetailsReadOnlyPopulator(EuGrantTransferRestService grantTransferRestService) {
        this.grantTransferRestService = grantTransferRestService;
    }

    @Override
    public GrantTransferDetailsReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        Optional<EuGrantTransferResource> grantTransferResource = grantTransferRestService.findDetailsByApplicationId(data.getApplication().getId()).getOptionalSuccessObject();
        if (grantTransferResource.isPresent()) {
            return new GrantTransferDetailsReadOnlyViewModel(
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
        return new GrantTransferDetailsReadOnlyViewModel(data, question);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(QuestionSetupType.GRANT_TRANSFER_DETAILS);
    }

}
