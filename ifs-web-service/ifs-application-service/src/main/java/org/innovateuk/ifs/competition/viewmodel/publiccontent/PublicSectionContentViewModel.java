package org.innovateuk.ifs.competition.viewmodel.publiccontent;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;

public abstract class PublicSectionContentViewModel {

    private PublicContentSectionType sectionType;
    private boolean published = false;

    public PublicContentSectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(PublicContentSectionType sectionType) {
        this.sectionType = sectionType;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getSectionPath() {
        return sectionType.getPath();
    }
}
