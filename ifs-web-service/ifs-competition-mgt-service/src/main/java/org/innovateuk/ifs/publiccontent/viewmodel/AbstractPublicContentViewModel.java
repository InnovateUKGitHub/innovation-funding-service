package org.innovateuk.ifs.publiccontent.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.competition.resource.CompetitionResource;


public abstract class AbstractPublicContentViewModel {

    private CompetitionResource competition;

    private PublicContentSectionResource section;

    private boolean published = false;

    private boolean readOnly = false;

    public CompetitionResource getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public PublicContentSectionResource getSection() {
        return section;
    }

    public void setSection(PublicContentSectionResource section) {
        this.section = section;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isComplete() { return PublicContentStatus.COMPLETE.equals(section.getType()); }

}
