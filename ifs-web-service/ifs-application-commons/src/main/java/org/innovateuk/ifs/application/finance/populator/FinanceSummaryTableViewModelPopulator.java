package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.populator.util.FinanceLinksUtil;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceSummaryTableRow;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceSummaryTableViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;

@Component
public class FinanceSummaryTableViewModelPopulator {

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

    @Autowired
    private FinanceLinksUtil financeLinksUtil;


    public FinanceSummaryTableViewModel populateSingleOrganisation(ApplicationResource application, CompetitionResource competition, OrganisationResource organisation) {
        List<OrganisationResource> organisations;
        boolean includeOrganisationNames;
        //We have to call this endpoint to initialise finances. Maybe the getApplicationFinances(long applicationId) should initialise finances also.
        List<ApplicationFinanceResource> finances = newArrayList(applicationFinanceRestService.getFinanceDetails(application.getId(), organisation.getId()).getSuccess());
        if (competition.isKtp()) {
            organisations = organisationRestService.getOrganisationsByApplicationId(application.getId()).getSuccess();
            finances = applicationFinanceRestService.getFinanceTotals(application.getId()).getSuccess();
            includeOrganisationNames = true;
        } else {
            organisations = newArrayList(organisation);
            includeOrganisationNames = false;
        }
        SectionResource financeSection = getFinanceSection(competition.getId());

        Map<Long, ApplicationFinanceResource> financeMap = finances
                .stream().collect(toMap(ApplicationFinanceResource::getOrganisation, Function.identity()));

        List<FinanceSummaryTableRow> rows = organisations.stream()
                .map(org -> toFinanceTableRow(org, financeMap, emptyMap(), application.getLeadOrganisationId(), financeSection, application, competition, empty()))
                .collect(toList());


        return new FinanceSummaryTableViewModel(application.getId(),
                competition,
                rows,
                true,
                application.isCollaborativeProject(),
                null,
                includeOrganisationNames);
    }

    public FinanceSummaryTableViewModel populateAllOrganisations(ApplicationResource application, CompetitionResource competition, List<ProcessRoleResource> processRoles, UserResource user) {
        Optional<ProcessRoleResource> currentApplicantRole = getCurrentUsersRole(processRoles, user);

        boolean open = application.isOpen() && competition.isOpen() && currentApplicantRole.isPresent();
        boolean readonly = !open;

        Map<Long, ApplicationFinanceResource> finances = applicationFinanceRestService.getFinanceTotals(application.getId()).getSuccess()
                .stream().collect(toMap(ApplicationFinanceResource::getOrganisation, Function.identity()));

        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(application.getId()).getSuccess();

        final Map<Long, Set<Long>> completedSections;
        final BigDecimal maximumFundingSought;
        if (!readonly) {
            completedSections = sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId()).getSuccess();
            maximumFundingSought = competitionApplicationConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess().getMaximumFundingSought();
        } else {
            completedSections = new HashMap<>();
            maximumFundingSought = null;
        }

        long leadOrganisationId = leadOrganisationId(processRoles);
        SectionResource financeSection = getFinanceSection(competition.getId());

        List<FinanceSummaryTableRow> rows = emptyList();
        if (financeSection != null) {
            rows = organisations.stream()
                    .map(organisation -> {
                        Optional<String> financeUrl = financesLink(organisation, application, competition, user, processRoles);
                        return toFinanceTableRow(organisation, finances, completedSections, leadOrganisationId, financeSection, application, competition, financeUrl);
                    })
                    .collect(toList());

            if (!application.isSubmitted()) {
                rows.addAll(pendingOrganisations(application.getId()));
            }
        }

        return new FinanceSummaryTableViewModel(application.getId(),
                competition,
                rows,
                readonly,
                application.isCollaborativeProject(),
                maximumFundingSought,
                true);
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

    private FinanceSummaryTableRow toFinanceTableRow(OrganisationResource organisation, Map<Long, ApplicationFinanceResource> finances,
                                                     Map<Long, Set<Long>> completedSections, long leadOrganisationId, SectionResource financeSection,
                                                     ApplicationResource application, CompetitionResource competition, Optional<String> financeLink) {
        Optional<ApplicationFinanceResource> finance = ofNullable(finances.get(organisation.getId()));
        Optional<ApplicationFinanceResource> leadFinance = ofNullable(finances.get(leadOrganisationId));
        boolean lead = organisation.getId().equals(leadOrganisationId);
        return new FinanceSummaryTableRow(
                organisation.getId(),
                organisation.getName(),
                organisationText(application, lead),
                finance.map(ApplicationFinanceResource::getTotal).orElse(BigDecimal.ZERO),
                finance.map(ApplicationFinanceResource::getGrantClaimPercentage).orElse(BigDecimal.ZERO),
                finance.map(ApplicationFinanceResource::getTotalFundingSought).orElse(BigDecimal.ZERO),
                calculateOtherFundingColumn(competition, finance),
                calculateContributionColumn(competition, finance, leadFinance),
                calculateContributionPercentageColumn(competition, finance, leadFinance),
                ofNullable(completedSections.get(organisation.getId()))
                        .map(completedIds -> completedIds.contains(financeSection.getId()))
                        .orElse(false),
                financeLink.isPresent(),
                financeLink.orElse(null)
        );
    }


    private BigDecimal calculateOtherFundingColumn(CompetitionResource competition, Optional<ApplicationFinanceResource> finance) {
        if (competition.isKtp()) {
            return finance.map(ApplicationFinanceResource::getTotalPreviousFunding).orElse(BigDecimal.ZERO);
        } else {
            return finance.map(ApplicationFinanceResource::getTotalOtherFunding).orElse(BigDecimal.ZERO);
        }
    }

    private BigDecimal calculateContributionColumn(CompetitionResource competition, Optional<ApplicationFinanceResource> finance, Optional<ApplicationFinanceResource> leadFinance) {
        if (competition.isKtp()) {
            if (finance.isPresent() && leadFinance.isPresent()) {
                if (finance.get().getOrganisation().equals(leadFinance.get().getOrganisation())) {
                    return BigDecimal.ZERO; //lead
                } else {
                    return leadFinance.map(ApplicationFinanceResource::getTotalContribution).orElse(BigDecimal.ZERO); // non-lead provides the contribution
                }
            } else {
                return BigDecimal.ZERO;
            }
        } else {
            return finance.map(ApplicationFinanceResource::getTotalContribution).orElse(BigDecimal.ZERO);
        }
    }

    private BigDecimal calculateContributionPercentageColumn(CompetitionResource competition, Optional<ApplicationFinanceResource> finance, Optional<ApplicationFinanceResource> leadFinance) {
        if (competition.isKtp()) {
            if (finance.isPresent() && leadFinance.isPresent()) {
                if (finance.get().getOrganisation().equals(leadFinance.get().getOrganisation())) {
                    return BigDecimal.ZERO; //lead
                } else {
                    return leadFinance.map(ApplicationFinanceResource::getGrantClaimPercentage)
                            .map(per -> BigDecimal.valueOf(100).subtract(per))
                            .orElse(BigDecimal.ZERO); // non-lead provides the contribution
                }
            } else {
                return BigDecimal.ZERO;
            }
        } else {
            return finance.map(ApplicationFinanceResource::getTotalContribution).orElse(BigDecimal.ZERO);
        }    }


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

    private Optional<String> financesLink(OrganisationResource organisation, ApplicationResource application, CompetitionResource competition,
                                          UserResource user, List<ProcessRoleResource> processRoles) {

        return competition.isKtp()
                ? financeLinksUtil.financesLink(organisation, processRoles, user, application, competition)
                : Optional.empty();
    }
}
