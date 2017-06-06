package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.forms.viewmodel.QuestionOrganisationDetailsViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Populating viewModel for Organisation Details
 */
@Component
public class OrganisationDetailsViewModelPopulator {

    @Autowired
    protected InviteRestService inviteRestService;

    public <R extends AbstractApplicantResource> QuestionOrganisationDetailsViewModel populateModel(final R applicantResource) {
        final SortedSet<OrganisationResource> organisations = getApplicationOrganisations(applicantResource);
        final List<String> activeApplicationOrganisationNames = organisations.stream().map(OrganisationResource::getName).collect(Collectors.toList());
        final List<String> pendingOrganisationNames = pendingInvitations(applicantResource.getApplication().getId()).stream()
                .map(ApplicationInviteResource::getInviteOrganisationNameConfirmedSafe)
                .distinct()
                .filter(orgName -> StringUtils.hasText(orgName)
                        && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());
        final Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(applicantResource);
        OrganisationResource foundLeadOrganisation = leadOrganisation.isPresent() ? leadOrganisation.get() : null;

        return new QuestionOrganisationDetailsViewModel(getAcademicOrganisations(organisations), organisations, pendingOrganisationNames, foundLeadOrganisation);
    }

    private SortedSet<OrganisationResource> getAcademicOrganisations(final SortedSet<OrganisationResource> organisations) {
        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        final Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);
        final ArrayList<OrganisationResource> organisationList = new ArrayList<>(organisations);

        return organisationList.stream()
                .filter(o -> OrganisationTypeEnum.RESEARCH.getId().equals(o.getOrganisationType()))
                .collect(Collectors.toCollection(supplier));
    }

    private <R extends AbstractApplicantResource> SortedSet<OrganisationResource> getApplicationOrganisations(final R applicantResource) {
        final Comparator<OrganisationResource> compareById =
                Comparator.comparingLong(OrganisationResource::getId);
        final Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        return applicantResource.getApplicants().stream()
                .filter(applicant -> applicant.getProcessRole().getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())
                        || applicant.getProcessRole().getRoleName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
                .map(ApplicantResource::getOrganisation)
                .collect(Collectors.toCollection(supplier));
    }

    private <R extends AbstractApplicantResource> Optional<OrganisationResource> getApplicationLeadOrganisation(R applicantResource) {
        return applicantResource.getApplicants().stream()
                .filter(applicant -> applicant.getProcessRole().getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ApplicantResource::getOrganisation)
                .findFirst();
    }

    private List<ApplicationInviteResource> pendingInvitations(final Long applicationId) {
        final RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(applicationId);

        return pendingAssignableUsersResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(0),
                success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                        .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                        .collect(Collectors.toList()));
    }
}
