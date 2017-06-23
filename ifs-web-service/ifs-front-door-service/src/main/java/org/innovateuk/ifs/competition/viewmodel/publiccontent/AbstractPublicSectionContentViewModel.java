package org.innovateuk.ifs.competition.viewmodel.publiccontent;

import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;

public abstract class AbstractPublicSectionContentViewModel {

    private PublicContentSectionType sectionType;
    private Boolean published = false;
    private String path;
    private String text;
    private Boolean isActive;

    public PublicContentSectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(PublicContentSectionType sectionType) {
        this.sectionType = sectionType;
    }

    public Boolean isPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
