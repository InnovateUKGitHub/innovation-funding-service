package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourOrganisationSectionViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

@Component
public class YourFundingSectionPopulator extends AbstractSectionPopulator<YourOrganisationSectionViewModel> {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected void populate(ApplicantSectionResource section, ApplicationForm form, YourOrganisationSectionViewModel viewModel, Model model) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        viewModel.setComplete(completedSectionIds.contains(section.getSection().getId()));
    }

    @Override
    protected YourOrganisationSectionViewModel createNew(ApplicantSectionResource section, ApplicationForm form) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        return new YourOrganisationSectionViewModel(section, formInputViewModelGenerator.fromSection(section, section, form), getNavigationViewModel(section), completedSectionIds.contains(section.getSection().getId()));
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.FUNDING_FINANCES;
    }
}

