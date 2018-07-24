package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.populator.AbstractSectionPopulator;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourFinancesSectionViewModel;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Your finances populator section view models.
 */
@Component
public class YourFinancesSectionPopulator extends AbstractSectionPopulator<YourFinancesSectionViewModel> {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Override
    protected YourFinancesSectionViewModel createNew(ApplicantSectionResource applicantSection, ApplicationForm form, Boolean readOnly, Optional<Long> applicantOrganisationId, Boolean readOnlyAllApplicantApplicationFinances) {
        return new YourFinancesSectionViewModel(applicantSection, Collections.emptyList(), getNavigationViewModel(applicantSection), readOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    @Override
    public void populateNoReturn(ApplicantSectionResource section,
                                 ApplicationForm form,
                                 YourFinancesSectionViewModel viewModel,
                                 Model model,
                                 BindingResult bindingResult,
                                 Boolean readOnly,
                                 Optional<Long> applicantOrganisationId) {
        ApplicantSectionResource yourOrganisation = findChildSectionByType(section, SectionType.ORGANISATION_FINANCES);
        ApplicantSectionResource yourFunding = findChildSectionByType(section, SectionType.FUNDING_FINANCES);
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());

        boolean yourFundingComplete = completedSectionIds.contains(yourFunding.getSection().getId());
        boolean yourOrganisationComplete = completedSectionIds.contains(yourOrganisation.getSection().getId());

        initializeApplicantFinances(section);
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(financeService, fileEntryRestService, section.getApplication().getId());
        BaseFinanceResource organisationFinances = organisationFinanceOverview.getFinancesByOrganisation().get(section.getCurrentApplicant().getOrganisation().getId());

        viewModel.setNotRequestingFunding(yourFundingComplete && yourOrganisationComplete && organisationFinances.getGrantClaimPercentage() != null && organisationFinances.getGrantClaimPercentage() == 0);
        viewModel.setCompletedSectionIds(completedSectionIds);
        viewModel.setOrganisationFinance(organisationFinances);
    }

    private void initializeApplicantFinances(ApplicantSectionResource section) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(section.getCurrentUser().getId(), section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        if (applicationFinanceResource == null) {
            financeService.addApplicationFinance(section.getCurrentUser().getId(), section.getApplication().getId());
        }
    }

    private ApplicantSectionResource findChildSectionByType(ApplicantSectionResource section, SectionType sectionType) {
        return section.getApplicantChildrenSections().stream().filter(child -> child.getSection().getType().equals(sectionType)).findAny().get();
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.FINANCE;
    }
}

