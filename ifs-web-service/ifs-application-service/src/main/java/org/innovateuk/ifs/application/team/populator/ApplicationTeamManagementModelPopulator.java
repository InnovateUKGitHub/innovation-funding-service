package org.innovateuk.ifs.application.team.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementApplicantRowViewModel;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamManagementViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.APPLICATION_TEAM;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Builds the model for the Application Team Update view.
 */
@Component
public class ApplicationTeamManagementModelPopulator {

    private InviteOrganisationRestService inviteOrganisationRestService;

    private ApplicationService applicationService;

    private QuestionRestService questionRestService;

    private UserService userService;
    
    @Autowired
    public ApplicationTeamManagementModelPopulator(InviteOrganisationRestService inviteOrganisationRestService,
                                         ApplicationService applicationService,
                                         QuestionRestService questionRestService,
                                         UserService userService) {
        this.inviteOrganisationRestService = inviteOrganisationRestService;
        this.applicationService = applicationService;
        this.questionRestService = questionRestService;
        this.userService = userService;
    }

    public ApplicationTeamManagementViewModel populateModelByOrganisationId(Long applicationId, Long organisationId, long loggedInUserId) {
        InviteOrganisationResource inviteOrganisationResource = getInviteOrganisationByOrganisationId(applicationId, organisationId).orElse(null);

        OrganisationResource leadOrganisationResource = getLeadOrganisation(applicationId);
        boolean requestForLeadOrganisation = isRequestForLeadOrganisation(organisationId, leadOrganisationResource);

        return populateModel(applicationId, loggedInUserId, leadOrganisationResource, requestForLeadOrganisation, inviteOrganisationResource);
    }

    public ApplicationTeamManagementViewModel populateModelByInviteOrganisationId(Long applicationId, Long inviteOrganisationId, long loggedInUserId) {
        InviteOrganisationResource inviteOrganisationResource = getInviteOrganisationByInviteOrganisationId(inviteOrganisationId);

        OrganisationResource leadOrganisationResource = getLeadOrganisation(applicationId);
        boolean requestForLeadOrganisation = isRequestForLeadOrganisation(inviteOrganisationResource, leadOrganisationResource);

        return populateModel(applicationId, loggedInUserId, leadOrganisationResource, requestForLeadOrganisation, inviteOrganisationResource);
    }

    private ApplicationTeamManagementViewModel populateModel(Long applicationId,
                                                             Long loggedInUserId,
                                                             OrganisationResource leadOrganisationResource,
                                                             boolean requestForLeadOrganisation,
                                                             InviteOrganisationResource inviteOrganisationResource) {
        ApplicationResource applicationResource = applicationService.getById(applicationId);
        UserResource leadApplicant = getLeadApplicant(applicationResource);
        boolean userLeadApplicant = isUserLeadApplicant(loggedInUserId, leadApplicant);

        if (requestForLeadOrganisation) {
            return populateModelForLeadOrganisation(leadOrganisationResource.getId(), leadOrganisationResource.getName(),
                    applicationResource, leadApplicant, userLeadApplicant, inviteOrganisationResource);
        }

        return populateModelForNonLeadOrganisation(applicationResource, userLeadApplicant, inviteOrganisationResource);
    }

    private ApplicationTeamManagementViewModel populateModelForLeadOrganisation(Long organisationId, String organisationName,
                                                                                ApplicationResource applicationResource,
                                                                                UserResource leadApplicant,
                                                                                boolean userLeadApplicant,
                                                                                InviteOrganisationResource inviteOrganisationResource) {
        List<ApplicationInviteResource> invites = ofNullable(inviteOrganisationResource)
                .map(InviteOrganisationResource::getInviteResources).orElse(emptyList());

        return new ApplicationTeamManagementViewModel(applicationResource.getId(),
                getApplicationTeamQuestion(applicationResource.getCompetition()),
                applicationResource.getName(),
                organisationId,
                ofNullable(inviteOrganisationResource).map(InviteOrganisationResource::getId).orElse(null),
                organisationName,
                true,
                userLeadApplicant,
                sortApplicants(combineLists(getLeadApplicantViewModel(leadApplicant), simpleMap(invites, applicationInviteResource ->
                        getApplicantViewModel(applicationInviteResource, userLeadApplicant)))),
                true
        );
    }

    private ApplicationTeamManagementViewModel populateModelForNonLeadOrganisation(ApplicationResource applicationResource,
                                                                                   boolean userLeadApplicant,
                                                                                   InviteOrganisationResource inviteOrganisationResource) {
        boolean organisationExists = inviteOrganisationResource.getOrganisation() != null;
        return new ApplicationTeamManagementViewModel(applicationResource.getId(),
                getApplicationTeamQuestion(applicationResource.getCompetition()),
                applicationResource.getName(),
                inviteOrganisationResource.getOrganisation(),
                inviteOrganisationResource.getId(),
                getOrganisationName(inviteOrganisationResource),
                false,
                userLeadApplicant,
                sortApplicants(simpleMap(inviteOrganisationResource.getInviteResources(), applicationInviteResource ->
                        getApplicantViewModel(applicationInviteResource, userLeadApplicant))),
                organisationExists);
    }

    private List<ApplicationTeamManagementApplicantRowViewModel> sortApplicants(List<ApplicationTeamManagementApplicantRowViewModel> applicants) {
        return applicants.stream()
                .sorted(getPendingOnNullFirstComparator())
                .collect(Collectors.toList());
    }

    private Comparator<ApplicationTeamManagementApplicantRowViewModel> getPendingOnNullFirstComparator() {
        return Comparator.comparing(ApplicationTeamManagementApplicantRowViewModel::getPendingSince, Comparator.nullsFirst(Comparator.naturalOrder()));
    }

    private ApplicationTeamManagementApplicantRowViewModel getApplicantViewModel(ApplicationInviteResource applicationInviteResource, boolean userLeadApplicant) {
        boolean pending = applicationInviteResource.getStatus() != InviteStatus.OPENED;
        return new ApplicationTeamManagementApplicantRowViewModel(applicationInviteResource.getId(), getApplicantName(applicationInviteResource),
                applicationInviteResource.getEmail(), false, pending, userLeadApplicant, applicationInviteResource.getSentOn());
    }

    private ApplicationTeamManagementApplicantRowViewModel getLeadApplicantViewModel(UserResource leadApplicant) {
        return new ApplicationTeamManagementApplicantRowViewModel(leadApplicant.getName(), leadApplicant.getEmail(), true, false, false, null);
    }

    private UserResource getLeadApplicant(ApplicationResource applicationResource) {
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(applicationResource.getId());
        return userService.findById(leadApplicantProcessRole.getUser());
    }

    private OrganisationResource getLeadOrganisation(long applicationId) {
        return applicationService.getLeadOrganisation(applicationId);
    }

    private Optional<InviteOrganisationResource> getInviteOrganisationByOrganisationId(long applicationId, long organisationId) {
        return inviteOrganisationRestService.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId)
                .toOptionalIfNotFound().getSuccess();
    }

    private InviteOrganisationResource getInviteOrganisationByInviteOrganisationId(long inviteOrganisationId) {
        return inviteOrganisationRestService.getById(inviteOrganisationId)
                .getSuccess();
    }

    private Long getApplicationTeamQuestion(long competitionId) {
        return questionRestService.getQuestionByCompetitionIdAndCompetitionSetupQuestionType(competitionId,
                APPLICATION_TEAM).handleSuccessOrFailure(failure -> null, QuestionResource::getId);
    }

    private String getOrganisationName(InviteOrganisationResource inviteOrganisationResource) {
        return isNotBlank(inviteOrganisationResource.getOrganisationNameConfirmed()) ?
                inviteOrganisationResource.getOrganisationNameConfirmed() : inviteOrganisationResource.getOrganisationName();
    }

    private String getApplicantName(ApplicationInviteResource applicationInviteResource) {
        return isNotBlank(applicationInviteResource.getNameConfirmed()) ?
                applicationInviteResource.getNameConfirmed() : applicationInviteResource.getName();
    }

    private boolean isUserLeadApplicant(long userId, UserResource leadApplicant) {
        return userId == leadApplicant.getId();
    }

    private boolean isRequestForLeadOrganisation(long requestedOrganisationId, OrganisationResource leadOrganisationResource) {
        return requestedOrganisationId == leadOrganisationResource.getId();
    }

    private boolean isRequestForLeadOrganisation(InviteOrganisationResource inviteOrganisationResource, OrganisationResource leadOrganisationResource) {
        return inviteOrganisationResource.getOrganisation() != null
                && inviteOrganisationResource.getOrganisation().equals(leadOrganisationResource.getId());
    }
}
