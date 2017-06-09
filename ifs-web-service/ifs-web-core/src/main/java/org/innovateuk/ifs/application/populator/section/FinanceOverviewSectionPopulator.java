package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.OpenSectionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.section.FinanceOverviewSectionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

/**
 * Finance overview section view models.
 */
@Component
public class FinanceOverviewSectionPopulator extends AbstractSectionPopulator<FinanceOverviewSectionViewModel> {

    @Autowired
    private OpenSectionModelPopulator openSectionModelPopulator;
    @Autowired
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected void populate(ApplicantSectionResource section, ApplicationForm form, FinanceOverviewSectionViewModel viewModel, Model model, BindingResult bindingResult, boolean readOnly) {
        viewModel.setOpenSectionViewModel((OpenSectionViewModel) openSectionModelPopulator.populateModel(form, model, bindingResult, section));
    }

    @Override
    protected FinanceOverviewSectionViewModel createNew(ApplicantSectionResource section, ApplicationForm form, boolean readOnly) {
        return new FinanceOverviewSectionViewModel(section, formInputViewModelGenerator.fromSection(section, section, form, readOnly), getNavigationViewModel(section), true);
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.OVERVIEW_FINANCES;
    }
}

