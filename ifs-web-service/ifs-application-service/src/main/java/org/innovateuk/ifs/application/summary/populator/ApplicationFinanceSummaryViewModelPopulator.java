package org.innovateuk.ifs.application.summary.populator;


import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class ApplicationFinanceSummaryViewModelPopulator {

    private FinanceService financeService;
    private FileEntryRestService fileEntryRestService;
    private OrganisationRestService organisationRestService;
    private ApplicationService applicationService;
    private SectionService sectionService;
    private OrganisationService organisationService;
    private ProcessRoleService processRoleService;
    private UserService userService;

    public ApplicationFinanceSummaryViewModelPopulator(ApplicationService applicationService,
                                                       SectionService sectionService,
                                                       FinanceService financeService,
                                                       FileEntryRestService fileEntryRestService,
                                                       OrganisationRestService organisationRestService,
                                                       OrganisationService organisationService,
                                                       ProcessRoleService processRoleService,
                                                       UserService userService) {
        this.applicationService = applicationService;
        this.sectionService = sectionService;
        this.financeService = financeService;
        this.fileEntryRestService = fileEntryRestService;
        this.organisationRestService = organisationRestService;
        this.organisationService = organisationService;
        this.processRoleService = processRoleService;
        this.userService = userService;
    }

    public ApplicationFinanceSummaryViewModel populate(long applicationId) {

        ApplicationResource application = applicationService.getById(applicationId);

        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(
                financeService,
                fileEntryRestService,
                applicationId
        );
        Map<FinanceRowType, BigDecimal> financeTotalPerType = organisationFinanceOverview.getTotalPerType();

        List<OrganisationResource> applicationOrganisations = getApplicationOrganisations(applicationId);

        SectionResource financeSection = sectionService.getFinanceSection(application.getCompetition());
        final boolean hasFinanceSection;
        final Long financeSectionId;
        if (financeSection == null) {
            hasFinanceSection = false;
            financeSectionId = null;
        } else {
            hasFinanceSection = true;
            financeSectionId = financeSection.getId();
        }

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());


        ProcessRoleResource leadApplicantUser = userService.getLeadApplicantProcessRoleOrNull(applicationId);
        OrganisationResource leadOrganisation = organisationService.getOrganisationById(leadApplicantUser.getOrganisationId());


//        Optional<OrganisationResource> userOrganisation = getUserOrganisation(user.getId(), userApplicationRoles);
        Set<Long> sectionsMarkedAsComplete = getCompletedSectionsForUserOrganisation(completedSectionsByOrganisation, leadOrganisation);


        return new ApplicationFinanceSummaryViewModel(
                application,
                hasFinanceSection,
                financeTotalPerType,
                applicationOrganisations,
                sectionsMarkedAsComplete,
                financeSectionId,
                leadOrganisation
                );
    }

    private List<OrganisationResource> getApplicationOrganisations(final Long applicationId) {
        return organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
    }

    private Set<Long> getCompletedSectionsForUserOrganisation(Map<Long, Set<Long>> completedSectionsByOrganisation, OrganisationResource userOrganisation) {
        return completedSectionsByOrganisation.getOrDefault(
                userOrganisation.getId(),
                new HashSet<>()
        );
    }

//    public Optional<OrganisationResource> getUserOrganisation(Long userId, List<ProcessRoleResource> userApplicationRoles) {
//
//        return userApplicationRoles.stream()
//                .filter(uar -> uar.getUser().equals(userId) && uar.getOrganisationId() != null)
//                .map(uar -> organisationService.getOrganisationById(uar.getOrganisationId()))
//                .findFirst();
//    }
}
