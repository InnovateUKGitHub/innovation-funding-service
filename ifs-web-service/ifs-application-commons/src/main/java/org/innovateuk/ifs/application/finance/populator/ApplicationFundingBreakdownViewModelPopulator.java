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
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
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
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private FinanceLinksUtil financeLinksUtil;

    public ApplicationFundingBreakdownViewModel populate(long applicationId, UserResource user) {

        Map<Long, BaseFinanceResource> finances = applicationFinanceRestService.getFinanceTotals(applicationId).getSuccess()
                .stream().collect(toMap(ApplicationFinanceResource::getOrganisation, Function.identity()));

        return viewModel(applicationId, finances, user, true,
                (application, competition) -> !application.isSubmitted() && !competition.isKtp());
    }

    public ApplicationFundingBreakdownViewModel populateFromProject(ProjectResource project, UserResource user) {

        Map<Long, BaseFinanceResource> finances = projectFinanceRestService.getProjectFinances(project.getId()).getSuccess()
                .stream().collect(toMap(ProjectFinanceResource::getOrganisation, Function.identity()));

        return viewModel(project.getApplication(), finances, user, false, (a, c) -> false);
    }

    private ApplicationFundingBreakdownViewModel viewModel(long applicationId, Map<Long, BaseFinanceResource> finances, UserResource user, boolean canIncludeFinanceLink,
                                                           BiPredicate<ApplicationResource, CompetitionResource> addPendingOrganisations) {

        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        boolean collaborativeProject = application.isCollaborativeProject();
        long leadOrganisationId = application.getLeadOrganisationId();

        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(application.getId()).getSuccess();
        List<ProcessRoleResource> processRoles = processRoleRestService.findProcessRole(application.getId()).getSuccess();
        List<FinanceRowType> types = competition.getFinanceRowTypes().stream().filter(FinanceRowType::isCost).collect(toList());

        List<BreakdownTableRow> rows = organisations.stream()
                .filter(organisation -> competition.isKtp() ? organisation.getId().equals(leadOrganisationId) : true)
                .map(organisation -> toFinanceTableRow(organisation, finances, leadOrganisationId, processRoles, user, application, competition, canIncludeFinanceLink))
                .collect(toList());

        if (addPendingOrganisations.test(application, competition)) {
            rows.addAll(pendingOrganisations(application.getId(), types));
        }

        return new ApplicationFundingBreakdownViewModel(application.getId(),
                competition.getName(),
                rows,
                collaborativeProject,
                competition.getFundingType() == FundingType.KTP,
                types);
    }



    private Collection<BreakdownTableRow> pendingOrganisations(long applicationId, List<FinanceRowType> types) {
        return inviteService.getPendingInvitationsByApplicationId(applicationId).stream()
                .filter(ApplicationInviteResource::isInviteNameConfirmed)
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .map((name) -> BreakdownTableRow.pendingOrganisation(name, types))
                .collect(toList());
    }

    private BreakdownTableRow toFinanceTableRow(OrganisationResource organisation, Map<Long, BaseFinanceResource> finances,
                                                long leadOrganisationId, List<ProcessRoleResource> processRoles, UserResource user,
                                                ApplicationResource application, CompetitionResource competition, boolean canIncludeFinanceLink) {
        Optional<BaseFinanceResource> finance = Optional.ofNullable(finances.get(organisation.getId()));
        Optional<String> financeLink;
        if (canIncludeFinanceLink) {
            financeLink = financeLinksUtil.financesLink(organisation, processRoles, user, application, competition);
        }  else {
            financeLink = Optional.empty();
        }
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
                finance.map(BaseFinanceResource::getTotal).orElse(BigDecimal.ZERO)
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

}