package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;

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
    public FinanceViewModel getFinanceViewModel(Long applicationId, List<QuestionResource> costsQuestions, Long userId, Form form, Long organisationId) {
        FinanceViewModel financeViewModel = new FinanceViewModel();
        ApplicationFinanceResource applicationFinanceResource = getOrganisationFinances(applicationId, costsQuestions, userId, organisationId);

        if (applicationFinanceResource != null) {
            OrganisationTypeResource organisationType = organisationTypeService.getForOrganisationId(applicationFinanceResource.getOrganisation()).getSuccess();
            financeViewModel.setOrganisationFinance(applicationFinanceResource.getFinanceOrganisationDetails());
            financeViewModel.setOrganisationFinanceSize(applicationFinanceResource.getOrganisationSize());
            financeViewModel.setOrganisationType(organisationType);
            financeViewModel.setOrganisationFinanceId(applicationFinanceResource.getId());
            financeViewModel.setOrganisationFinanceTotal(applicationFinanceResource.getTotal());
            financeViewModel.setMaximumGrantClaimPercentage(applicationFinanceResource.getMaximumFundingLevel());
            financeViewModel.setFinanceView("finance");
            financeViewModel.setFinanceQuestions(CollectionFunctions.simpleToMap(costsQuestions, this::costTypeForQuestion));
            addGrantClaim(financeViewModel, applicationFinanceResource);
        }
        return financeViewModel;
    }

    private void addGrantClaim(FinanceViewModel financeViewModel, ApplicationFinanceResource applicationFinanceResource) {
        if (applicationFinanceResource.getGrantClaim() != null) {
            financeViewModel.setOrganisationGrantClaimPercentage(ofNullable(applicationFinanceResource.getGrantClaim().getGrantClaimPercentage()).orElse(0));
            financeViewModel.setOrganisationGrantClaimPercentageId(applicationFinanceResource.getGrantClaim().getId());
        }
    }

    protected ApplicationFinanceResource getOrganisationFinances(Long applicationId, List<QuestionResource> costsQuestions, Long userId, Long organisationId) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId, organisationId);
        if (applicationFinanceResource == null) {
            financeService.addApplicationFinance(userId, applicationId);
            // ugly fix since the addApplicationFinance method does not return the correct results.
            applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        }
        return applicationFinanceResource;
    }

    private FinanceRowType costTypeForQuestion(QuestionResource question) {
        List<FormInputResource> formInputs = formInputRestService.getByQuestionIdAndScope(question.getId(), APPLICATION).getSuccess();
        if (formInputs.isEmpty()) {
            return null;
        }
        for (FormInputResource formInput : formInputs) {
            FormInputType formInputType = formInput.getType();
            if (StringUtils.isEmpty(formInputType)) {
                continue;
            }
            try {
                return FinanceRowType.fromType(formInputType);
            } catch (IllegalArgumentException e) {
                LOG.trace("no finance row type for form input type", e);
                continue;
            }
        }
        return null;
    }
}
