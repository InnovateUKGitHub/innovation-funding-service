package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.populator.util.FinanceLinksUtil;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.BreakdownTableRow;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
    private FinanceLinksUtil financeLinksUtil;

    public ApplicationFundingBreakdownViewModel populate(long applicationId, UserResource user) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess();

        Map<Long, ApplicationFinanceResource> finances = applicationFinanceRestService.getFinanceTotals(applicationId).getSuccess()
                    .stream().collect(toMap(ApplicationFinanceResource::getOrganisation, Function.identity()));

        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(applicationId).getSuccess();
        long leadOrganisationId = application.getLeadOrganisationId();
        List<FinanceRowType> types = competition.getFinanceRowTypes().stream().filter(FinanceRowType::isCost).collect(toList());

        List<BreakdownTableRow> rows = organisations.stream()
                .filter(organisation -> competition.isKtp() ? organisation.getId().equals(leadOrganisationId) : true)
                .map(organisation -> toFinanceTableRow(organisation, finances, leadOrganisationId, processRoles, user, application, competition))
                .collect(toList());

        if (!application.isSubmitted() && !competition.isKtp()) {
            rows.addAll(pendingOrganisations(applicationId, types));
        }

        return new ApplicationFundingBreakdownViewModel(applicationId,
                competition.getName(),
                rows,
                application.isCollaborativeProject(),
                competition.getFundingType() == FundingType.KTP,
                types,
                finances.values().stream().anyMatch(ApplicationFinanceResource::isVatRegistered));
    }

    private Collection<BreakdownTableRow> pendingOrganisations(long applicationId, List<FinanceRowType> types) {
        return inviteService.getPendingInvitationsByApplicationId(applicationId).stream()
                .filter(ApplicationInviteResource::isInviteNameConfirmed)
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .map((name) -> BreakdownTableRow.pendingOrganisation(name, types))
                .collect(toList());
    }

    private BreakdownTableRow toFinanceTableRow(OrganisationResource organisation, Map<Long, ApplicationFinanceResource> finances, long leadOrganisationId, List<ProcessRoleResource> processRoles, UserResource user, ApplicationResource application, CompetitionResource competition) {
        Optional<ApplicationFinanceResource> finance = Optional.ofNullable(finances.get(organisation.getId()));
        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, processRoles, user, application, competition);
        boolean lead = organisation.getId().equals(leadOrganisationId);
        return new BreakdownTableRow(
                organisation.getId(),
                organisation.getName(),
                organisationText(application, lead),
                financeLink.isPresent(),
                financeLink.orElse(null),
                competition.getFinanceRowTypes().stream()
                    .filter(FinanceRowType::isCost)
                        .collect(toMap(Function.identity(),
                                type -> finance.map(f -> f.getFinanceOrganisationDetails().get(type).getTotal()).orElse(BigDecimal.ZERO)
                        )),
                finance.map(ApplicationFinanceResource::getTotal).orElse(BigDecimal.ZERO)
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

    private BigDecimal getCategoryOrZero(ApplicationFinanceResource appFinance, FinanceRowType labour) {
        return Optional.ofNullable(appFinance.getFinanceOrganisationDetails(labour))
                .map(FinanceRowCostCategory::getTotal)
                .orElse(BigDecimal.ZERO);
    }

}