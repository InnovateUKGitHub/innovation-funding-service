package org.innovateuk.ifs.application.viewmodel.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Your organisation section view model.
 */
public class YourOrganisationSectionViewModel extends AbstractSectionViewModel {
    private boolean complete;

    public YourOrganisationSectionViewModel(ApplicantSectionResource applicantResource, List<AbstractFormInputViewModel> formInputViewModels, NavigationViewModel navigationViewModel, boolean allReadOnly, Optional<Long> applicantOrganisationId, boolean readOnlyAllApplicantApplicationFinances) {
        super(applicantResource, formInputViewModels, navigationViewModel, allReadOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public AbstractFormInputViewModel getOrganisationSizeFormInputViewModel() {
        return getByType(FormInputType.ORGANISATION_SIZE);
    }

    public AbstractFormInputViewModel getFinancialEndYearFormInputViewModel() {
        return getByType(FormInputType.FINANCIAL_YEAR_END);
    }

    public List<AbstractFormInputViewModel> getFinanceOverviewRows() {
        return getAllByType(FormInputType.FINANCIAL_OVERVIEW_ROW);
    }

    public List<AbstractFormInputViewModel> getStandardInputViewModels() {
        return getAllByNotType(FormInputType.FINANCIAL_OVERVIEW_ROW, FormInputType.ORGANISATION_SIZE, FormInputType.FINANCIAL_YEAR_END);
    }

    public AbstractFormInputViewModel getByType(FormInputType type) {
        return getFormInputViewModels().stream().filter(viewModel -> viewModel.getFormInput().getType().equals(type)).findAny().orElse(null);
    }

    public List<AbstractFormInputViewModel> getAllByType(FormInputType... types) {
        return getFormInputViewModels().stream().filter(viewModel -> Arrays.asList(types).contains(viewModel.getFormInput().getType())).collect(Collectors.toList());
    }

    public List<AbstractFormInputViewModel> getAllByNotType(FormInputType... types) {
        return getFormInputViewModels().stream().filter(viewModel -> !Arrays.asList(types).contains(viewModel.getFormInput().getType())).collect(Collectors.toList());
    }
}

