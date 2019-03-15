package org.innovateuk.ifs.application.populator.granttransfer;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantAgreementSummaryViewModel;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantTransferDetailsSummaryViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.innovateuk.ifs.granttransfer.service.ActionTypeRestService;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GrantTransferSummaryPopulator {

    private final EuGrantTransferRestService grantTransferRestService;
    private final ActionTypeRestService actionTypeRestService;

    public GrantTransferSummaryPopulator(EuGrantTransferRestService grantTransferRestService, ActionTypeRestService actionTypeRestService) {
        this.grantTransferRestService = grantTransferRestService;
        this.actionTypeRestService = actionTypeRestService;
    }

    public GrantTransferDetailsSummaryViewModel populateDetails(ApplicationResource application) {
        Optional<EuGrantTransferResource> grantTransferResource = grantTransferRestService.findDetailsByApplicationId(application.getId()).getOptionalSuccessObject();

        if (grantTransferResource.isPresent()) {
            return new GrantTransferDetailsSummaryViewModel(
                    grantTransferResource.get().getGrantAgreementNumber(),
                    grantTransferResource.get().getParticipantId(),
                    grantTransferResource.get().getProjectName(),
                    grantTransferResource.get().getProjectStartDate(),
                    grantTransferResource.get().getProjectEndDate(),
                    grantTransferResource.get().getFundingContribution(),
                    grantTransferResource.get().getProjectCoordinator(),
                    grantTransferResource
                            .map(EuGrantTransferResource::getActionType)
                            .map(EuActionTypeResource::getName)
                            .orElse(null)
            );
        }
        return GrantTransferDetailsSummaryViewModel.empty();
    }

    public GrantAgreementSummaryViewModel populateAgreement(ApplicationResource application) {
        Optional<FileEntryResource> grantAgreement = grantTransferRestService.findGrantAgreement(application.getId()).getOptionalSuccessObject();
        return new GrantAgreementSummaryViewModel(grantAgreement.map(FileEntryResource::getName).orElse(null));
    }
}
