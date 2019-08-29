package org.innovateuk.ifs.application.readonly.populator;

import com.google.common.collect.Multimap;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamOrganisationReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamUserReadOnlyViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

import static com.google.common.collect.Multimaps.index;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.user.resource.Role.applicantProcessRoles;

@Component
public class ApplicationTeamReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<ApplicationTeamReadOnlyViewModel> {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private OrganisationRestService organisationRestService;


    @Override
    public ApplicationTeamReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        List<ProcessRoleResource> processRoles = userRestService.findProcessRole(data.getApplication().getId()).getSuccess()
                .stream()
                .filter(role -> applicantProcessRoles().contains(role.getRole()))
                .collect(toList());
        List<InviteOrganisationResource> inviteOrganisationResources = emptyList();
        if (data.getApplicantProcessRole().isPresent()) {
            inviteOrganisationResources = inviteRestService.getInvitesByApplication(data.getApplication().getId()).getSuccess();
        }
        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(data.getApplication().getId()).getSuccess();

        Multimap<Long, ProcessRoleResource> organisationToProcessRole = index(processRoles, ProcessRoleResource::getOrganisationId);
        Map<Long, InviteOrganisationResource> organisationToInvite = inviteOrganisationResources.stream()
                .filter(organisationInvite -> organisationInvite.getOrganisation() != null)
                .collect(toMap(InviteOrganisationResource::getOrganisation, Function.identity()));

        List<ApplicationTeamOrganisationReadOnlyViewModel> organisationViewModels = organisations.stream()
                .map(organisation -> toOrganisationTeamViewModel(organisation, organisationToProcessRole.get(organisation.getId()), organisationToInvite.get(organisation.getId())))
                .collect(toList());

        organisationViewModels.addAll(inviteOrganisationResources.stream()
                .filter(invite -> invite.getOrganisation() == null)
                .map(this::toInviteOrganisationTeamViewModel)
                .collect(toList()));

        return new ApplicationTeamReadOnlyViewModel(data, question, organisationViewModels);

    }

    private ApplicationTeamOrganisationReadOnlyViewModel toOrganisationTeamViewModel(OrganisationResource organisation, Collection<ProcessRoleResource> processRoles, InviteOrganisationResource organisationInvite) {
        List<ApplicationTeamUserReadOnlyViewModel> userRows = processRoles.stream()
                .map(ApplicationTeamUserReadOnlyViewModel::fromProcessRole)
                .collect(toList());

        Optional<InviteOrganisationResource> maybeOrganisationInvite = ofNullable(organisationInvite);
        if (maybeOrganisationInvite.isPresent()) {
            userRows.addAll(organisationInvite.getInviteResources()
                    .stream()
                    .filter(invite -> invite.getStatus() == InviteStatus.SENT)
                    .map(ApplicationTeamUserReadOnlyViewModel::fromInvite)
                    .collect(toList()));
        }

        return new ApplicationTeamOrganisationReadOnlyViewModel(organisation.getName(),
                organisation.getOrganisationTypeName(),
                userRows,
                true);
    }

    private ApplicationTeamOrganisationReadOnlyViewModel toInviteOrganisationTeamViewModel(InviteOrganisationResource organisationInvite) {
        List<ApplicationTeamUserReadOnlyViewModel> inviteRows = organisationInvite.getInviteResources().stream()
                .map(ApplicationTeamUserReadOnlyViewModel::fromInvite)
                .collect(toList());

        return new ApplicationTeamOrganisationReadOnlyViewModel(organisationInvite.getOrganisationName(), null, inviteRows, false);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(QuestionSetupType.APPLICATION_TEAM);
    }
}
