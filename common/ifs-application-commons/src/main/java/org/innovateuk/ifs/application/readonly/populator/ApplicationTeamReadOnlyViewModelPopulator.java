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
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

import static com.google.common.collect.Multimaps.index;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.applicantProcessRoles;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
//TODO- Remove this when feature toggle for EDI is removed
public class ApplicationTeamReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<ApplicationTeamReadOnlyViewModel> {

    @Value("${ifs.edi.update.enabled}")
    private boolean ediUpdateEnabled;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationOrganisationAddressRestService applicationOrganisationAddressRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    public ApplicationTeamReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        boolean internalUser = data.getUser().isInternalUser();
        boolean shouldShowTeamMember = settings.isIncludeTeamMember();
        List<ProcessRoleResource> applicationProcessRoles = processRoleRestService.findProcessRole(data.getApplication().getId()).getSuccess();
        List<ProcessRoleResource> processRoles = applicationProcessRoles.stream()
                .filter(role -> applicantProcessRoles().contains(role.getRole()))
                .collect(toList());
        List<InviteOrganisationResource> inviteOrganisationResources = emptyList();


        boolean hasEDIQuestions = competitionRestService.hasEDIQuestion(question.getCompetition()).getSuccess();
        ediUpdateEnabled = ediUpdateEnabled && !hasEDIQuestions;


        if (showInvites(data)) {
            inviteOrganisationResources = inviteRestService.getInvitesByApplication(data.getApplication().getId()).getSuccess();
        }

        Optional<ProcessRoleResource> ktaProcessRole = Optional.empty();
        String ktaPhoneNumber = null;

        if (data.getCompetition().isKtp()) {
            ktaProcessRole = applicationProcessRoles.stream()
                    .filter(role -> role.getRole().isKta())
                    .findAny();

            if (internalUser && ktaProcessRole.isPresent()) {
                ktaPhoneNumber = getPhoneNumber(ktaProcessRole.get().getUserEmail());
            }
        }

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

        return new ApplicationTeamReadOnlyViewModel(data, question, organisationViewModels, ktaProcessRole, ktaPhoneNumber,
                internalUser, shouldShowTeamMember, ediUpdateEnabled);
    }

    private boolean showInvites(ApplicationReadOnlyData data) {
        return !data.getApplication().isSubmitted() &&
                (data.getUsersProcessRole().map(pr -> applicantProcessRoles().contains(pr.getRole())).orElse(false)
                        || data.getUser().hasAnyRoles(SUPPORT, IFS_ADMINISTRATOR));
    }

    private ApplicationTeamOrganisationReadOnlyViewModel toOrganisationTeamViewModel(ApplicationResource application, OrganisationResource organisation, Collection<ProcessRoleResource> processRoles, InviteOrganisationResource organisationInvite, boolean internalUser) {
        List<ApplicationTeamUserReadOnlyViewModel> userRows = processRoles.stream()
                .map(pr -> {
                        UserResource user = userRestService.retrieveUserById(pr.getUser()).getSuccess();
                        return ApplicationTeamUserReadOnlyViewModel.fromProcessRole(pr, internalUser ? user.getPhoneNumber() : null, user.getEdiStatus());
                       })
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
