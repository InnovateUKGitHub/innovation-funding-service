package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.BreakdownTableRow;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.HttpServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.user.resource.Role.*;

@Component
public class ApplicationFundingBreakdownViewModelPopulator {

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
    private InviteService inviteService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private HttpServletUtil httpServletUtil;

    public ApplicationFundingBreakdownViewModel populate(long applicationId, UserResource user) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess();

        Map<Long, ApplicationFinanceResource> finances = applicationFinanceRestService.getFinanceTotals(applicationId).getSuccess()
                    .stream().collect(toMap(ApplicationFinanceResource::getOrganisation, Function.identity()));

        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
        long leadOrganisationId = application.getLeadOrganisationId();

        List<BreakdownTableRow> rows = organisations.stream()
                .map(organisation -> toFinanceTableRow(organisation, finances, leadOrganisationId, processRoles, user, application, competition))
                .collect(toList());

        if (!application.isSubmitted()) {
            rows.addAll(pendingOrganisations(applicationId));
        }

        return new ApplicationFundingBreakdownViewModel(applicationId,
                competition.getName(),
                rows,
                application.isCollaborativeProject(),
                competition.getFinanceRowTypes(),
                finances.values().stream().anyMatch(ApplicationFinanceResource::isVatRegistered));
    }


    private Optional<ProcessRoleResource> getCurrentUsersRole(List<ProcessRoleResource> processRoles, UserResource user) {
        return processRoles.stream()
                .filter(role -> role.getUser().equals(user.getId()))
                .findFirst();
    }

    private Collection<BreakdownTableRow> pendingOrganisations(long applicationId) {
        return inviteService.getPendingInvitationsByApplicationId(applicationId).stream()
                .filter(ApplicationInviteResource::isInviteNameConfirmed)
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .map(BreakdownTableRow::pendingOrganisation)
                .collect(toList());
    }

    private BreakdownTableRow toFinanceTableRow(OrganisationResource organisation, Map<Long, ApplicationFinanceResource> finances, long leadOrganisationId, List<ProcessRoleResource> processRoles, UserResource user, ApplicationResource application, CompetitionResource competition) {
        Optional<ApplicationFinanceResource> finance = Optional.ofNullable(finances.get(organisation.getId()));
        Optional<String> financeLink = financesLink(organisation, processRoles, user, application, competition);
        boolean lead = organisation.getId().equals(leadOrganisationId);
        return new BreakdownTableRow(
                organisation.getId(),
                organisation.getName(),
                organisationText(application, lead),
                financeLink.isPresent(),
                financeLink.orElse(null),
                finance.map(ApplicationFinanceResource::getTotal).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, LABOUR)).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, OVERHEADS)).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, PROCUREMENT_OVERHEADS)).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, MATERIALS)).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, CAPITAL_USAGE)).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, SUBCONTRACTING_COSTS)).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, TRAVEL)).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, OTHER_COSTS)).orElse(BigDecimal.ZERO),
                finance.map(appFinance -> getCategoryOrZero(appFinance, VAT)).orElse(BigDecimal.ZERO)
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

    private Optional<String> financesLink(OrganisationResource organisation, List<ProcessRoleResource> processRoles, UserResource user, ApplicationResource application, CompetitionResource competition) {
        Optional<ProcessRoleResource> currentUserRole = getCurrentUsersRole(processRoles, user);

        UserResource authenticatedUser = userAuthenticationService.getAuthenticatedUser(httpServletUtil.request());
        if (authenticatedUser.isInternalUser() || authenticatedUser.getRoles().contains(STAKEHOLDER) || authenticatedUser.getRoles().contains(EXTERNAL_FINANCE)) {
            if (!application.isSubmitted()) {
                if (authenticatedUser.getRoles().contains(IFS_ADMINISTRATOR) || authenticatedUser.getRoles().contains(SUPPORT) || authenticatedUser.getRoles().contains(EXTERNAL_FINANCE)) {
                    return Optional.of(internalLink(application.getId(), organisation));
                }
            } else {
                return Optional.of(internalLink(application.getId(), organisation));
            }
        }
        if (currentUserRole.isPresent()) {
            if (applicantProcessRoles().contains(currentUserRole.get().getRole())
                    && currentUserRole.get().getOrganisationId().equals(organisation.getId())) {
                return Optional.of(applicantLink(application.getId()));
            }

            if (assessorProcessRoles().contains(currentUserRole.get().getRole())
                    && DETAILED.equals(competition.getAssessorFinanceView())) {
                return Optional.of(assessorLink(application, organisation));
            }
        }
        return Optional.empty();
    }

    private String assessorLink(ApplicationResource application, OrganisationResource organisation) {
        return format("/assessment/application/%d/detailed-finances/organisation/%d", application.getId(), organisation.getId());
    }

    private String internalLink(long applicationId, OrganisationResource organisation) {
        return format("/application/%d/form/%s/%d", applicationId, SectionType.FINANCE.name(), organisation.getId());
    }

    private String applicantLink(long applicationId) {
        return format("/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private BigDecimal getCategoryOrZero(ApplicationFinanceResource appFinance, FinanceRowType labour) {
        return Optional.ofNullable(appFinance.getFinanceOrganisationDetails(labour))
                .map(FinanceRowCostCategory::getTotal)
                .orElse(BigDecimal.ZERO);
    }

}