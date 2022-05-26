package org.innovateuk.ifs.management.publiccontent.viewmodel;


import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * View model for public content menu.
 */
@Setter
@Getter
public class PublicContentMenuViewModel {

    private static String COMPETITION_OVERVIEW_URL = "%s/competition/%d/overview";

    private static String PRIVATE_COMPETITION_OVERVIEW_URL = "%s/competition/%d/overview/%s";

    private ZonedDateTime publishDate;

    private List<PublicContentSectionResource> sections;

    private CompetitionResource competition;

    private Boolean inviteOnly;

    private String webBaseUrl;

    private String hash;

    public Boolean isInviteOnly() {
        return inviteOnly;
    }

    public boolean hasBeenPublished() {
        return publishDate != null;
    }

    public boolean disablePublishButton() {
        return sections.stream()
                .anyMatch(section -> PublicContentStatus.IN_PROGRESS.equals(section.getStatus()));
    }

    public boolean isSectionComplete(PublicContentSectionResource contentSectionResource) {
        return PublicContentStatus.COMPLETE.equals(contentSectionResource.getStatus());
    }

    public String getCompetitionURL() {
        return isInviteOnly()?
                String.format(PRIVATE_COMPETITION_OVERVIEW_URL, webBaseUrl, competition.getId(), this.hash) :
                String.format(COMPETITION_OVERVIEW_URL, webBaseUrl, competition.getId());
    }

}
