package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceSummaryTableRow;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;

@Component
public class ApplicationFinanceSummaryViewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private CompetitionApplicationConfigRestService competitionApplicationConfigRestService;

    public ApplicationFinanceSummaryViewModel populate(long applicationId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess();
        Optional<ProcessRoleResource> currentApplicantRole = getCurrentUsersRole(processRoles, user);

        boolean open = application.isOpen() && competition.isOpen() && currentApplicantRole.isPresent();

        Map<Long, ApplicationFinanceResource> finances = applicationFinanceRestService.getFinanceTotals(applicationId).getSuccess()
                .stream().collect(toMap(ApplicationFinanceResource::getOrganisation, Function.identity()));

        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();

        Map<Long, Set<Long>> completedSections = sectionStatusRestService.getCompletedSectionsByOrganisation(applicationId).getSuccess();
        
        long leadOrganisationId = leadOrganisationId(processRoles);
        SectionResource financeSection = getFinanceSection(competition.getId());

        List<FinanceSummaryTableRow> rows = emptyList();
        if (financeSection != null) {
            rows = organisations.stream()
                    .map(organisation -> toFinanceTableRow(organisation, finances, completedSections, leadOrganisationId, financeSection, application))
                    .collect(toList());

            if (!application.isSubmitted()) {
                rows.addAll(pendingOrganisations(applicationId));
            }
        }

        CompetitionApplicationConfigResource competitionApplicationConfigResource
                = competitionApplicationConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();

        return new ApplicationFinanceSummaryViewModel(applicationId, competition, rows, !open,
                application.isCollaborativeProject(),
                currentApplicantRole.map(ProcessRoleResource::getOrganisationId).orElse(null),
                competitionApplicationConfigResource.getMaximumFundingSought());
    }

    private Optional<ProcessRoleResource> getCurrentUsersRole(List<ProcessRoleResource> processRoles, UserResource user) {
        return processRoles.stream()
                .filter(role -> role.getUser().equals(user.getId()))
                .filter(role -> applicantProcessRoles().contains(role.getRole()))
                .findFirst();
    }

    private Collection<FinanceSummaryTableRow> pendingOrganisations(long applicationId) {
        return inviteService.getPendingInvitationsByApplicationId(applicationId).stream()
                .filter(ApplicationInviteResource::isInviteNameConfirmed)
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .map(FinanceSummaryTableRow::pendingOrganisation)
                .collect(toList());
    }

    private long leadOrganisationId(List<ProcessRoleResource> processRoles) {
        return processRoles.stream()
                .filter(role -> LEADAPPLICANT.equals(role.getRole()))
                .findFirst()
                .orElseThrow(ObjectNotFoundException::new)
                .getOrganisationId();
    }

    private FinanceSummaryTableRow toFinanceTableRow(OrganisationResource organisation, Map<Long, ApplicationFinanceResource> finances, Map<Long, Set<Long>> completedSections, long leadOrganisationId, SectionResource financeSection, ApplicationResource application) {
        Optional<ApplicationFinanceResource> finance = ofNullable(finances.get(organisation.getId()));
        boolean lead = organisation.getId().equals(leadOrganisationId);
        return new FinanceSummaryTableRow(
                organisation.getId(),
                organisation.getName(),
                organisationText(application, lead),
                finance.map(ApplicationFinanceResource::getTotal).orElse(BigDecimal.ZERO),
                finance.map(ApplicationFinanceResource::getGrantClaimPercentage).orElse(BigDecimal.ZERO),
                finance.map(ApplicationFinanceResource::getTotalFundingSought).orElse(BigDecimal.ZERO),
                finance.map(ApplicationFinanceResource::getTotalOtherFunding).orElse(BigDecimal.ZERO),
                finance.map(ApplicationFinanceResource::getTotalContribution).orElse(BigDecimal.ZERO),
                ofNullable(completedSections.get(organisation.getId()))
                        .map(completedIds -> completedIds.contains(financeSection.getId()))
                        .orElse(false)
        );
    }

    private String organisationText(ApplicationResource application, boolean lead) {
        if (!application.isCollaborativeProject()) {
            return "Organisation";
        } else if (lead) {
            return "Lead organisation";
        } else {
            return "Partner";
        }
    }

    private SectionResource getFinanceSection(long competitionId) {
        List<SectionResource> sections = sectionRestService.getSectionsByCompetitionIdAndType(competitionId, SectionType.FINANCE).getSuccess();
        if (sections.size() == 1) {
            return sections.get(0);
        }
        return null;
    }
}
