package org.innovateuk.ifs.project.activitylog.viewmodel;

import java.time.ZonedDateTime;

public class ActivityLogEntryViewModel {

    private final String title;
    private final String organisationName;
    private final String userText;
    private final ZonedDateTime createdOn;
    private final String linkText;
    private final String linkUrl;
    private final boolean displayLink;

    public ActivityLogEntryViewModel(String title, String organisationName, String userText, ZonedDateTime createdOn, String linkText, String linkUrl, boolean displayLink) {
        this.title = title;
        this.organisationName = organisationName;
        this.userText = userText;
        this.createdOn = createdOn;
        this.linkText = linkText;
        this.linkUrl = linkUrl;
        this.displayLink = displayLink;
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
}
