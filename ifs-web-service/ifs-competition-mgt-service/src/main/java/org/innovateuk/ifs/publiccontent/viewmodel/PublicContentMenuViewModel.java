package org.innovateuk.ifs.publiccontent.viewmodel;


import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PublicContentMenuViewModel {
    public static final DateTimeFormatter PUBLISH_DATE_FORMAT = DateTimeFormatter.ofPattern("hh:mma dd MMMM YYYY");

    private LocalDateTime publishDate;

    private List<PublicContentSectionResource> sections;

    private CompetitionResource competition;

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
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

    public String getPublishDateText() {
        return publishDate.format(PUBLISH_DATE_FORMAT);
    }

    private void doNOthering() {}

    public boolean hasBeenPublished() {
        return publishDate != null;
    }
}
