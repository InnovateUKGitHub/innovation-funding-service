package org.innovateuk.ifs.project.activitylog.populator;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.activitylog.service.ActivityLogRestService;
import org.innovateuk.ifs.project.activitylog.viewmodel.ActivityLogEntryViewModel;
import org.innovateuk.ifs.project.activitylog.viewmodel.ActivityLogViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.activitylog.resource.ActivityType.*;
import static org.innovateuk.ifs.project.activitylog.populator.ActivityLogUrlHelper.url;
import static org.innovateuk.ifs.user.resource.Role.*;
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
    private MessageSource messageSource;

    private static final Set<ActivityType> STAKEHOLDER_INNOVATION_SUPPORT_TYPES = EnumSet.of(APPLICATION_SUBMITTED, APPLICATION_INTO_PROJECT_SETUP, PROJECT_DETAILS_COMPLETE,
            PROJECT_MANAGER_NOMINATED, FINANCE_CONTACT_NOMINATED, ORGANISATION_ADDED, ORGANISATION_REMOVED, DOCUMENT_APPROVED, MONITORING_OFFICER_ASSIGNED, SPEND_PROFILE_APPROVED, FINANCE_REVIEWER_ADDED, GRANT_OFFER_LETTER_APPROVED);

    private static final Set<ActivityType> COMP_ADMIN_TYPES = Sets.union(STAKEHOLDER_INNOVATION_SUPPORT_TYPES,
            EnumSet.of(DOCUMENT_UPLOADED, DOCUMENT_REJECTED, SPEND_PROFILE_GENERATED, GRANT_OFFER_LETTER_UPLOADED, GRANT_OFFER_LETTER_PUBLISHED, GRANT_OFFER_LETTER_SIGNED, GRANT_OFFER_LETTER_REJECTED)
    );

    public ActivityLogViewModel populate(long projectId, UserResource user) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
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
                        url(activity, project),
                        userCanSeeLink(activity, user),
                        activity.getActivityType()
                ))
                .collect(toList());

        return new ActivityLogViewModel(
                project,
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

    private boolean userCanSeeLink(ActivityLogResource activity, UserResource user) {
        if (activity.isOrganisationRemoved() && ActivityLogUrlHelper.linkInvalidIfOrganisationRemoved(activity)) {
            return false;
        } else  if (user.hasRole(PROJECT_FINANCE)) {
            return true;
        } else if (user.hasRole(COMP_ADMIN)) {
            return COMP_ADMIN_TYPES.contains(activity.getActivityType());
        } else {
            return STAKEHOLDER_INNOVATION_SUPPORT_TYPES.contains(activity.getActivityType());
        }
    }

    private String title(ActivityLogResource activity) {
        String queryType = ofNullable(activity.getQueryType())
                .map(FinanceChecksSectionType::getDisplayName)
                .orElse("");
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
        if (log.isInternalUser() || log.isExternalFinanceUser()) {
            return internalUserText(log);
        } else {
            return externalUserText(log, projectUserResources, partnerOrganisationResources);
        }
    }

    private String internalUserText(ActivityLogResource log) {
        String role = log.isIfsAdmin() ? IFS_ADMINISTRATOR.getDisplayName()
                : log.getAuthoredByRoles().iterator().next().getDisplayName();
        return log.getAuthoredByName() + ", " + role;
    }

    private String externalUserText(ActivityLogResource log, List<ProjectUserResource> projectUserResources, List<PartnerOrganisationResource> partnerOrganisationResources) {
        Supplier<Stream<ProjectUserResource>> projectUsers = () -> projectUserResources
                .stream()
                .filter(pu -> pu.getUser().equals(log.getAuthoredBy()));
        String organisationName = organisationNameOfProjectUser(projectUsers, partnerOrganisationResources);
        String role = projectUsers.get().anyMatch(ProjectUserResource::isProjectManager) ? "Project manager"
                : projectUsers.get().anyMatch(ProjectUserResource::isFinanceContact) ? "Finance contact"
                : "Partner";
        if (organisationName == null) {
            organisationName = log.getOrganisationName();
        }
        return format("%s, %s for %s", log.getAuthoredByName(), role, organisationName);
    }

    private String organisationNameOfProjectUser(Supplier<Stream<ProjectUserResource>> projectUsers, List<PartnerOrganisationResource> partnerOrganisationResources) {
        return projectUsers.get()
                .findAny()
                .flatMap(pu -> partnerOrganisationResources.stream()
                        .filter(po -> po.getOrganisation().equals(pu.getOrganisation()))
                        .findAny())
                .map(PartnerOrganisationResource::getOrganisationName)
                .orElse(null);
    }

}
