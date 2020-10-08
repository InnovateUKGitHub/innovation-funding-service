package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;

@Component
public class ApplicationFinanceSummaryViewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private FinanceSummaryTableViewModelPopulator financeSummaryTableViewModelPopulator;

    public ApplicationFinanceSummaryViewModel populate(long applicationId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(applicationId).getSuccess();
        Optional<ProcessRoleResource> currentApplicantRole = getCurrentUsersRole(processRoles, user);

        return new ApplicationFinanceSummaryViewModel(competition, financeSummaryTableViewModelPopulator.populateAllOrganisations(application, competition, processRoles, user),
                currentApplicantRole.map(ProcessRoleResource::getOrganisationId).orElse(null));
    }

    private Optional<ProcessRoleResource> getCurrentUsersRole(List<ProcessRoleResource> processRoles, UserResource user) {
        return processRoles.stream()
                .filter(role -> role.getUser().equals(user.getId()))
                .filter(role -> applicantProcessRoles().contains(role.getRole()))
                .findFirst();
    }
}
