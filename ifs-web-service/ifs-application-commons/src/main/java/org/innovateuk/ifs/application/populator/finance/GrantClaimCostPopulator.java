package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.finance.GrantClaimCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for grant claim form inputs.
 */
@Component
public class GrantClaimCostPopulator extends AbstractCostPopulator<GrantClaimCostViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.FINANCE;
    }

    @Override
    protected GrantClaimCostViewModel createNew() {
        return new GrantClaimCostViewModel();
    }

    @Override
    protected void populateCost(AbstractApplicantResource resource, GrantClaimCostViewModel viewModel, ApplicationFinanceResource organisationFinances) {
        if (organisationFinances != null) {
            viewModel.setMaximumGrantClaimPercentage(organisationFinances.getMaximumFundingLevel());
            viewModel.setOrganisationGrantClaimPercentage(organisationFinances.getGrantClaim().getGrantClaimPercentage());
            viewModel.setOrganisationGrantClaimPercentageId(organisationFinances.getGrantClaim().getId());
        }
    }
}
