package org.innovateuk.ifs.project.activitylog.populator;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.service.ActivityLogRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.activitylog.viewmodel.ActivityLogEntryViewModel;
import org.innovateuk.ifs.project.activitylog.viewmodel.ActivityLogViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.negate;

@Component
public class ActivityLogViewModelPopulator {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ActivityLogRestService activityLogRestService;

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private MessageSource messageSource;

    public ActivityLogViewModel populate(long projectId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        List<PartnerOrganisationResource> partnerOrganisationResources = partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).getSuccess();
        List<ProjectUserResource> projectUserResources = projectRestService.getProjectUsersForProject(projectId).getSuccess();
        List<ActivityLogResource> activities = activityLogRestService.findByApplicationId(project.getApplication()).getSuccess();

        List<ActivityLogEntryViewModel> views = activities.stream()
                  .map(activity -> new ActivityLogEntryViewModel(
                            title(activity),
                             activity.getOrganisationName(),
                             userText(activity, projectUserResources, partnerOrganisationResources),
                             activity.getCreatedOn(),
                             linkText(activity),
                             url(activity, project)
                  ))
                  .collect(toList());

        return new ActivityLogViewModel(
                project.getCompetition(),
                project.getApplication(),
                project.getId(),
                project.getName(),
                competition.getName(),
                partnerOrganisationResources.stream()
                    .filter(PartnerOrganisationResource::isLeadOrganisation)
                    .findFirst()
                    .map(PartnerOrganisationResource::getOrganisationName)
                    .orElse(""),
                partnerOrganisationResources.stream()
                        .filter(negate(PartnerOrganisationResource::isLeadOrganisation))
                        .map(PartnerOrganisationResource::getOrganisationName)
                        .collect(joining(", ")),
                views
        );

    }

    private String title(ActivityLogResource activity) {
        String queryType = ofNullable(activity.getQueryType())
                .map(FinanceChecksSectionType::getDisplayName)
                .orElse(null);
        return messageSource.getMessage(format("ifs.activity.log.%s.title", activity.getActivityType().name()),
                new Object[]{queryType},
                Locale.getDefault());
    }

    private String linkText(ActivityLogResource activity) {
        String queryType = ofNullable(activity.getQueryType())
                .map(FinanceChecksSectionType::getDisplayName)
                .map(String::toLowerCase)
                .orElse(null);
        String documentName = ofNullable(activity.getDocumentConfigName())
                    .map(String::toLowerCase)
                    .orElse(null);

        return messageSource.getMessage(format("ifs.activity.log.%s.link", activity.getActivityType().name()),
                new Object[]{queryType, documentName},
                Locale.getDefault());
    }

    private String userText(ActivityLogResource log, List<ProjectUserResource> projectUserResources, List<PartnerOrganisationResource> partnerOrganisationResources) {
        if (log.getCreatedByRoles().stream().anyMatch(role -> Role.internalRoles().contains(role))) {
            String role = log.getCreatedByRoles().contains(IFS_ADMINISTRATOR) ? IFS_ADMINISTRATOR.getDisplayName()
                    : log.getCreatedByRoles().iterator().next().getDisplayName();
            return log.getCreatedByName() + ", " + role;
        } else {
            Supplier<Stream<ProjectUserResource>> projectUsers = () -> projectUserResources
                    .stream()
                    .filter(pu -> pu.getUser().equals(log.getCreatedBy()));
            String organisationName = projectUsers.get()
                    .findAny()
                    .flatMap(pu -> partnerOrganisationResources.stream()
                            .filter(po -> po.getOrganisation().equals(pu.getOrganisation()))
                            .findAny())
                    .map(PartnerOrganisationResource::getOrganisationName)
                    .orElse(null);
            String role = projectUsers.get().anyMatch(pu -> pu.getRole().equals(PROJECT_MANAGER.getId())) ? "Project manager"
                    : projectUsers.get().anyMatch(pu -> pu.getRole().equals(FINANCE_CONTACT.getId())) ? "Finance contact"
                    : "Partner";
            return format("%s, %s for %s", log.getCreatedByName(), role, organisationName);
        }
    }

    private String url(ActivityLogResource log, ProjectResource project) {
        switch (log.getActivityType()) {
            case APPLICATION_SUBMITTED:
                return format("/management/competition/%d/application/%d?origin=PROJECT_SETUP_MANAGEMENT_ACTIVITY_LOG&projectId=%d", project.getCompetition(), project.getApplication(), project.getId());
            case APPLICATION_INTO_PROJECT_SETUP:
                return format("/project-setup-management/competition/%d/status/all?applicationSearchString=%d", project.getCompetition(), project.getApplication());
            case PROJECT_DETAILS_COMPLETE:
            case FINANCE_REVIEWER_ADDED:
            case MANAGED_OFFLINE:
            case COMPLETE_OFFLINE:
            case WITHDRAWN:
            case ON_HOLD:
            case RESUMED_FROM_ON_HOLD:
                return format("/project-setup-management/competition/%d/project/%d/details", project.getCompetition(), project.getId());
            case PROJECT_MANAGER_NOMINATED:
            case FINANCE_CONTACT_NOMINATED:
                return format("/project-setup-management/competition/%d/project/%d/team", project.getCompetition(), project.getId());
            case DOCUMENT_UPLOADED:
            case DOCUMENT_APPROVED:
                return format("/project-setup-management/project/%d/document/config/%d", project.getId(), log.getDocumentConfig());
            case MONITORING_OFFICER_ASSIGNED:
                return format("/project-setup-management/project/%d/monitoring-officer", project.getId());
            case BANK_DETAILS_SUBMITTED:
            case BANK_DETAILS_APPROVED:
                return format("/project-setup-management/project/%d/organisation/%d/review-bank-details", project.getId(), log.getOrganisation());
            case VIABILITY_APPROVED:
                return format("/project-setup-management/project/%d/finance-check/organisation/%d/viability", project.getId(), log.getOrganisation());
            case ELIGIBILITY_APPROVED:
                return format("/project-setup-management/project/%d/finance-check/organisation/%d/eligibility", project.getId(), log.getOrganisation());
            case FINANCE_QUERY:
            case FINANCE_QUERY_RESPONDED:
                return format("/project-setup-management/project/%d/finance-check/organisation/%d/query?query_section=%s", project.getId(), log.getOrganisation(), log.getQueryType().name());
            case SPEND_PROFILE_GENERATED:
            case SPEND_PROFILE_EDIT:
            case SPEND_PROFILE_COMPLETE:
                return format("/project-setup-management/project/%d/finance-check", project.getId());
            case SPEND_PROFILE_SENT:
            case SPEND_PROFILE_APPROVED:
                return format("/project-setup-management/project/%d/spend-profile/approval", project.getId());
            case GRANT_OFFER_LETTER_UPLOADED:
            case GRANT_OFFER_LETTER_PUBLISHED:
            case GRANT_OFFER_LETTER_SIGNED:
            case GRANT_OFFER_LETTER_APPROVED:
                return format("/project-setup-management/project/%d/grant-offer-letter/send", project.getId());
            default:
                return "#";
        }
    }
}
