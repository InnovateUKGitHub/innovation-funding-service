package org.innovateuk.ifs.application.readonly.populator;

import com.google.common.collect.Multimap;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamOrganisationReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamUserReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationOrganisationAddressRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
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
import static org.innovateuk.ifs.user.resource.Role.*;

@Component
public class ApplicationTeamReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<ApplicationTeamReadOnlyViewModel> {

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationOrganisationAddressRestService applicationOrganisationAddressRestService;

    @Override
    public ApplicationTeamReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        boolean internalUser = data.getUser().isInternalUser();
        List<ProcessRoleResource> applicationProcessRoles = userRestService.findProcessRole(data.getApplication().getId()).getSuccess();
        List<ProcessRoleResource> processRoles = applicationProcessRoles.stream()
                .filter(role -> applicantProcessRoles().contains(role.getRole()))
                .collect(toList());
        List<InviteOrganisationResource> inviteOrganisationResources = emptyList();
        if (showInvites(data)) {
            inviteOrganisationResources = inviteRestService.getInvitesByApplication(data.getApplication().getId()).getSuccess();
        }
        Optional<ProcessRoleResource> ktaProcessRole = applicationProcessRoles.stream()
                .filter(role -> KNOWLEDGE_TRANSFER_ADVISOR == role.getRole())
                .findAny();

        List<OrganisationResource> organisations = organisationRestService.getOrganisationsByApplicationId(data.getApplication().getId()).getSuccess();

        Multimap<Long, ProcessRoleResource> organisationToProcessRole = index(processRoles, ProcessRoleResource::getOrganisationId);
        Map<Long, InviteOrganisationResource> organisationToInvite = inviteOrganisationResources.stream()
                .filter(organisationInvite -> organisationInvite.getOrganisation() != null)
                .collect(toMap(InviteOrganisationResource::getOrganisation, Function.identity()));

        List<ApplicationTeamOrganisationReadOnlyViewModel> organisationViewModels = organisations.stream()
                .map(organisation -> toOrganisationTeamViewModel(data.getApplication(), organisation, organisationToProcessRole.get(organisation.getId()), organisationToInvite.get(organisation.getId()), internalUser))
                .collect(toList());

        organisationViewModels.addAll(inviteOrganisationResources.stream()
                .filter(invite -> invite.getOrganisation() == null)
                .map(this::toInviteOrganisationTeamViewModel)
                .collect(toList()));

        return new ApplicationTeamReadOnlyViewModel(data, question, organisationViewModels, ktaProcessRole, internalUser);
    }

    private boolean showInvites(ApplicationReadOnlyData data) {
        return !data.getApplication().isSubmitted() &&
                (data.getApplicantProcessRole().isPresent()
                || data.getUser().hasAnyRoles(SUPPORT, IFS_ADMINISTRATOR));
    }

    private ApplicationTeamOrganisationReadOnlyViewModel toOrganisationTeamViewModel(ApplicationResource application, OrganisationResource organisation, Collection<ProcessRoleResource> processRoles, InviteOrganisationResource organisationInvite, boolean internalUser) {
        List<ApplicationTeamUserReadOnlyViewModel> userRows = processRoles.stream()
                .map(pr -> ApplicationTeamUserReadOnlyViewModel.fromProcessRole(pr, internalUser ? getPhoneNumber(pr.getUserEmail()) : null))
                .collect(toList());

        Optional<InviteOrganisationResource> maybeOrganisationInvite = ofNullable(organisationInvite);
        if (maybeOrganisationInvite.isPresent()) {
            userRows.addAll(organisationInvite.getInviteResources()
                    .stream()
                    .filter(invite -> invite.getStatus() == InviteStatus.SENT)
                    .map(ApplicationTeamUserReadOnlyViewModel::fromInvite)
                    .collect(toList()));
        }

        AddressResource address = null;
        if (organisation.isInternational()) {
            address = applicationOrganisationAddressRestService.getAddress(application.getId(), organisation.getId(), OrganisationAddressType.INTERNATIONAL).getSuccess();
        }

        return new ApplicationTeamOrganisationReadOnlyViewModel(organisation.getName(),
                organisation.getOrganisationTypeName(),
                userRows,
                true,
                address);
    }

    private String getPhoneNumber(String userEmail) {
        return userRestService.findUserByEmail(userEmail).getOptionalSuccessObject().map(UserResource::getPhoneNumber).orElse(null);
    }

    private ApplicationTeamOrganisationReadOnlyViewModel toInviteOrganisationTeamViewModel(InviteOrganisationResource organisationInvite) {
        List<ApplicationTeamUserReadOnlyViewModel> inviteRows = organisationInvite.getInviteResources().stream()
                .map(ApplicationTeamUserReadOnlyViewModel::fromInvite)
                .collect(toList());

        return new ApplicationTeamOrganisationReadOnlyViewModel(organisationInvite.getOrganisationName(), null, inviteRows, false, null);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(QuestionSetupType.APPLICATION_TEAM);
    }
}
