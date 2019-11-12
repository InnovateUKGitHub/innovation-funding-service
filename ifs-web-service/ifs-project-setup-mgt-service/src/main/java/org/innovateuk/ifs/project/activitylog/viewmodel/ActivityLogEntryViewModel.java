package org.innovateuk.ifs.project.activitylog.viewmodel;

import org.innovateuk.ifs.activitylog.resource.ActivityType;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.activitylog.resource.ActivityType.ORGANISATION_ADDED;
import static org.innovateuk.ifs.activitylog.resource.ActivityType.ORGANISATION_REMOVED;

public class ActivityLogEntryViewModel {

    private final String title;
    private final String organisationName;
    private final String userText;
    private final ZonedDateTime createdOn;
    private final String linkText;
    private final String linkUrl;
    private final boolean displayLink;
    private final ActivityType activityType;

    public ActivityLogEntryViewModel(String title, String organisationName, String userText, ZonedDateTime createdOn, String linkText, String linkUrl, boolean displayLink, ActivityType activityType) {

        this.title = title;
        this.organisationName = organisationName;
        this.userText = userText;
        this.createdOn = createdOn;
        this.linkText = linkText;
        this.linkUrl = linkUrl;
        this.displayLink = displayLink;
        this.activityType = activityType;
    }

    public String getTitle() {
        return title;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getUserText() {
        return userText;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public String getLinkText() {
        return linkText;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public boolean isDisplayLink() {
        return displayLink;
    }

    public String getView() {
        switch (activityType) {
            case ORGANISATION_ADDED:
                return ORGANISATION_ADDED.name();
            case ORGANISATION_REMOVED:
                return ORGANISATION_REMOVED.name();
            default:
                return "DEFAULT";
        }
    }
}
