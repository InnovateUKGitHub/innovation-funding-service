package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.AbstractSectionPopulator;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourProjectLocationSectionViewModel;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

/**
 * Your organisation populator section view models.
 */
@Component
public class YourProjectLocationSectionPopulator extends AbstractSectionPopulator<YourProjectLocationSectionViewModel> {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    protected void populateNoReturn(ApplicantSectionResource section, ApplicationForm form, YourProjectLocationSectionViewModel viewModel, Model model, BindingResult bindingResult, Boolean readOnly, Optional<Long> applicantOrganisationId) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        viewModel.setComplete(completedSectionIds.contains(section.getSection().getId()));

        String projectLocation = applicationFinanceRestService.getApplicationFinance(viewModel.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId()).getSuccess().getWorkPostcode();
        viewModel.setProjectLocationValue(projectLocation);

        viewModel.setReadonly(viewModel.isComplete() || !section.getCompetition().isOpen() || !section.getApplication().isOpen());
    }

    @Override
    protected YourProjectLocationSectionViewModel createNew(ApplicantSectionResource section, ApplicationForm form, Boolean readOnly, Optional<Long> applicantOrganisationId, Boolean readOnlyAllApplicantApplicationFinances) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        return new YourProjectLocationSectionViewModel(section, formInputViewModelGenerator.fromSection(section, section, form, readOnly), getNavigationViewModel(section), completedSectionIds.contains(section.getSection().getId()), applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.PROJECT_LOCATION;
    }
}

