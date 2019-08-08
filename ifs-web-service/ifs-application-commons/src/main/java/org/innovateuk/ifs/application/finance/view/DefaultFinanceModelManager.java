package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

/**
 * Managing all the view attributes for the finances
 */
@Component
public class DefaultFinanceModelManager implements FinanceModelManager {

    private static final Log LOG = LogFactory.getLog(DefaultFinanceModelManager.class);

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private OrganisationTypeRestService organisationTypeService;

    @Autowired
    private FinanceViewHandlerProvider financeViewHandlerProvider;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    public FinanceViewModel getFinanceViewModel(Long applicationId, Long userId, Form form, Long organisationId) {
        FinanceViewModel financeViewModel = new FinanceViewModel();
        ApplicationFinanceResource applicationFinanceResource = getOrganisationFinances(applicationId, userId, organisationId);

        if (applicationFinanceResource != null) {
            OrganisationTypeResource organisationType = organisationTypeService.getForOrganisationId(applicationFinanceResource.getOrganisation()).getSuccess();
            financeViewModel.setOrganisationFinance(applicationFinanceResource.getFinanceOrganisationDetails());
            financeViewModel.setOrganisationFinanceSize(applicationFinanceResource.getOrganisationSize());
            financeViewModel.setOrganisationType(organisationType);
            financeViewModel.setOrganisationFinanceId(applicationFinanceResource.getId());
            financeViewModel.setOrganisationFinanceTotal(applicationFinanceResource.getTotal());
            financeViewModel.setMaximumGrantClaimPercentage(applicationFinanceResource.getMaximumFundingLevel());
            financeViewModel.setFinanceView("finance");
            addGrantClaim(financeViewModel, applicationFinanceResource);
        }
        return financeViewModel;
    }

    private void addGrantClaim(FinanceViewModel financeViewModel, ApplicationFinanceResource applicationFinanceResource) {
        if (applicationFinanceResource.getGrantClaim() != null) {
            financeViewModel.setOrganisationGrantClaimPercentage(ofNullable(applicationFinanceResource.getGrantClaimPercentage()).orElse(0));
            financeViewModel.setOrganisationGrantClaimPercentageId(applicationFinanceResource.getGrantClaim().getId());
        }
    }

    protected ApplicationFinanceResource getOrganisationFinances(Long applicationId, Long userId, Long organisationId) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId, organisationId);
        if (applicationFinanceResource == null) {
            financeService.addApplicationFinance(userId, applicationId);
            // ugly fix since the addApplicationFinance method does not return the correct results.
            applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        }
        return applicationFinanceResource;
    }
}
