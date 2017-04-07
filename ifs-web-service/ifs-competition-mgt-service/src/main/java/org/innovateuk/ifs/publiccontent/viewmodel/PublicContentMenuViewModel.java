package org.innovateuk.ifs.publiccontent.viewmodel;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * View model for public content menu.
 */
public class PublicContentMenuViewModel {

    private ZonedDateTime publishDate;

    private List<PublicContentSectionResource> sections;

    private CompetitionResource competition;

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public List<PublicContentSectionResource> getSections() {
        return sections;
    }

    public void setSections(List<PublicContentSectionResource> sections) {
        this.sections = sections;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public boolean hasBeenPublished() {
        return publishDate != null;
    }

    public boolean disablePublishButton() {
        return sections.stream()
                .filter(section -> PublicContentStatus.IN_PROGRESS.equals(section.getStatus()))
                .findAny()
                .isPresent();
    }

    public boolean isSectionComplete(PublicContentSectionResource contentSectionResource) {
        return PublicContentStatus.COMPLETE.equals(contentSectionResource.getStatus());
    }
}
