package org.innovateuk.ifs.application.finance.populator.util;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.HttpServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.ALL;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.applicantProcessRoles;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.assessorProcessRoles;
import static org.innovateuk.ifs.user.resource.Role.*;

@Component
public class FinanceLinksUtil {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private HttpServletUtil httpServletUtil;

    @Autowired
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Autowired
    private SupporterAssignmentRestService supporterAssignmentRestService;

    public Optional<String> financesLink(OrganisationResource organisation, List<ProcessRoleResource> processRoles, UserResource user, ApplicationResource application, CompetitionResource competition) {
        Optional<ProcessRoleResource> currentUserRole = getCurrentUsersRole(processRoles, user);

        UserResource authenticatedUser = userAuthenticationService.getAuthenticatedUser(httpServletUtil.request());
        if (authenticatedUser.isInternalUser() || authenticatedUser.getRoles().contains(STAKEHOLDER) || authenticatedUser.getRoles().contains(EXTERNAL_FINANCE)) {
            if (!application.isSubmitted()) {
                if (authenticatedUser.getRoles().contains(IFS_ADMINISTRATOR) || authenticatedUser.getRoles().contains(SUPPORT) || authenticatedUser.getRoles().contains(EXTERNAL_FINANCE)) {
                    return Optional.of(organisationIdInLink(application.getId(), organisation));
                }
            } else {
                return Optional.of(organisationIdInLink(application.getId(), organisation));
            }
        } else if (authenticatedUser.getRoles().contains(SUPPORTER)) {
            Optional<SupporterAssignmentResource> supporterAssignment = supporterAssignmentRestService.getAssignment(authenticatedUser.getId(), application.getId())
                    .toOptionalIfNotFound().getSuccess();
            if (supporterAssignment.isPresent()) {
                return Optional.of(organisationIdInLink(application.getId(), organisation));
            }
        }

        if (currentUserRole.isPresent()) {
            if (applicantProcessRoles().contains(currentUserRole.get().getRole())) {
                if (competition.isKtp()) {
                    //All KTP users can see each others finances.
                    return Optional.of(organisationIdInLink(application.getId(), organisation));
                } else if (currentUserRole.get().getOrganisationId().equals(organisation.getId())) {
                    return Optional.of(applicantsOrganisationLink(application.getId()));
                }
            }

            if (currentUserRole.get().getRole().isKta()) {
                return Optional.of(organisationIdInLink(application.getId(), organisation));
            }

            CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();

            if (assessorProcessRoles().contains(currentUserRole.get().getRole())) {
                if (DETAILED == competitionAssessmentConfigResource.getAssessorFinanceView()) {
                    return Optional.of(assessorLink(application, organisation));
                } else if (ALL == competitionAssessmentConfigResource.getAssessorFinanceView()) {
                    return Optional.of(organisationIdInLink(application.getId(), organisation));
                }
            }
        }

        return Optional.empty();
    }

    private String assessorLink(ApplicationResource application, OrganisationResource organisation) {
        return format("/assessment/application/%d/detailed-finances/organisation/%d", application.getId(), organisation.getId());
    }

    private String organisationIdInLink(long applicationId, OrganisationResource organisation) {
        return format("/application/%d/form/%s/%d", applicationId, SectionType.FINANCE.name(), organisation.getId());
    }

    private String applicantsOrganisationLink(long applicationId) {
        return format("/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }

    private Optional<ProcessRoleResource> getCurrentUsersRole(List<ProcessRoleResource> processRoles, UserResource user) {
        return processRoles.stream()
                .filter(role -> role.getUser().equals(user.getId()))
                .findFirst();
    }

}
