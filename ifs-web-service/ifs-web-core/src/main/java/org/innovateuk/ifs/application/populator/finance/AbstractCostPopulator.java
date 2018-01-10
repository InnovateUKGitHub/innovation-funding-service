package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.populator.forminput.AbstractFormInputPopulator;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.viewmodel.finance.AbstractCostViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract populator for cost form inputs.
 */
public abstract class AbstractCostPopulator<M extends AbstractCostViewModel> extends AbstractFormInputPopulator<M> {

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private FinanceHandler financeHandler;

    @Override
    protected void populate(AbstractApplicantResource resource, M viewModel) {
        ApplicationFinanceResource organisationFinances = applicationFinanceRestService.getFinanceDetails(resource.getApplication().getId(), resource.getCurrentApplicant().getOrganisation().getId()).getSuccessObjectOrThrowException();
        FinanceRowCostCategory category = organisationFinances.getFinanceOrganisationDetails(viewModel.getFinanceRowType());
        if (viewModel.getQuestion().getType().equals(QuestionType.COST)) {
            FinanceRowItem costItem = financeHandler.getFinanceFormHandler(resource.getCurrentApplicant().getOrganisation().getOrganisationType()).addCostWithoutPersisting(resource.getApplication().getId(), resource.getCurrentUser().getId(), viewModel.getQuestion().getId());
            category.addCost(costItem);
        }
        viewModel.setCostCategory(category);
        viewModel.setViewmode((viewModel.isComplete() || viewModel.isReadonly()) ? "readonly" : "edit");

        populateCost(resource, viewModel, organisationFinances);
    }

    protected void populateCost(AbstractApplicantResource resource, M viewModel, ApplicationFinanceResource organisationFinances) {
        //Can be overridden by subclass.
    }

}
