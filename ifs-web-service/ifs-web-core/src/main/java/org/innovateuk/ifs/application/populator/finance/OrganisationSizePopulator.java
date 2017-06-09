package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.populator.forminput.AbstractFormInputPopulator;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.finance.OrganisationSizeViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.OrganisationDetailsRestService;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Populator for organisation size form inputs.
 */
@Component
public class OrganisationSizePopulator extends AbstractFormInputPopulator<OrganisationSizeViewModel> {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private OrganisationDetailsRestService organisationDetailsRestService;
    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;


    @Override
    public FormInputType type() {
        return FormInputType.ORGANISATION_SIZE;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, OrganisationSizeViewModel viewModel, boolean readOnly) {
        sectionService.getSectionsForCompetitionByType(resource.getCompetition().getId(), SectionType.FUNDING_FINANCES).stream().findAny().ifPresent(fundingSection -> {
            List<Long> completedSectionIds = sectionService.getCompleted(resource.getApplication().getId(), resource.getCurrentApplicant().getOrganisation().getId());
            viewModel.setOrganisationSizeAlert(completedSectionIds.contains(fundingSection.getId()));
        });
        viewModel.setOrganisationSizes(organisationDetailsRestService.getOrganisationSizes().getSuccessObjectOrThrowException());
        ApplicationFinanceResource applicationFinanceResource = applicationFinanceRestService.getFinanceDetails(resource.getApplication().getId(), resource.getCurrentApplicant().getOrganisation().getId()).getSuccessObjectOrThrowException();
        viewModel.setOrganisationFinanceSize(applicationFinanceResource.getOrganisationSize());

    }

    @Override
    protected OrganisationSizeViewModel createNew() {
        return new OrganisationSizeViewModel();
    }
}
