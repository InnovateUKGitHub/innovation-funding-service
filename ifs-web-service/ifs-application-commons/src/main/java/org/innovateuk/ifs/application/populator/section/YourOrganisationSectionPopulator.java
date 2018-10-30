package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourOrganisationSectionViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;

/**
 * Your organisation populator section view models.
 */
@Component
public class YourOrganisationSectionPopulator extends AbstractSectionPopulator<YourOrganisationSectionViewModel> {

    private final SectionService sectionService;
    private final FormInputViewModelGenerator formInputViewModelGenerator;
    private final OrganisationRestService organisationRestService;

    public YourOrganisationSectionPopulator(final ApplicationNavigationPopulator navigationPopulator,
                                            final SectionService sectionService,
                                            final FormInputViewModelGenerator formInputViewModelGenerator,
                                            final OrganisationRestService organisationRestService) {
        super(navigationPopulator);
        this.sectionService = sectionService;
        this.formInputViewModelGenerator = formInputViewModelGenerator;
        this.organisationRestService = organisationRestService;
    }

    private boolean isBusinessOrganisation(Long organisationId) {
        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();
        return organisationResource.getOrganisationType() == OrganisationTypeEnum.BUSINESS.getId();
    }

    @Override
    protected void populateNoReturn(ApplicantSectionResource section, ApplicationForm form, YourOrganisationSectionViewModel viewModel, Model model, BindingResult bindingResult, Boolean readOnly, Optional<Long> applicantOrganisationId) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        viewModel.setComplete(completedSectionIds.contains(section.getSection().getId()));
    }

    @Override
    protected YourOrganisationSectionViewModel createNew(ApplicantSectionResource section, ApplicationForm form, Boolean readOnly, Optional<Long> applicantOrganisationId, Boolean readOnlyAllApplicantApplicationFinances) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        boolean stateAidEligibility = TRUE.equals(section.getCompetition().getStateAid());
        return new YourOrganisationSectionViewModel(section,
                formInputViewModelGenerator.fromSection(section, section, form, readOnly),
                getNavigationViewModel(section),
                completedSectionIds.contains(section.getSection().getId()),
                applicantOrganisationId,
                readOnlyAllApplicantApplicationFinances,
                isBusinessOrganisation(section.getCurrentApplicant().getOrganisation().getId()),
                stateAidEligibility);
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.ORGANISATION_FINANCES;
    }
}

