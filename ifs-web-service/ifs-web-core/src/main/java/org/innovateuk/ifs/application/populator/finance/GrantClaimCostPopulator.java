package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.finance.GrantClaimCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

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
        viewModel.setMaximumGrantClaimPercentage(organisationFinances.getMaximumFundingLevel());
        viewModel.setOrganisationGrantClaimPercentage(ofNullable(organisationFinances.getGrantClaim().getGrantClaimPercentage()).orElse(0));
        viewModel.setOrganisationGrantClaimPercentageId(organisationFinances.getGrantClaim().getId());
    }
}
