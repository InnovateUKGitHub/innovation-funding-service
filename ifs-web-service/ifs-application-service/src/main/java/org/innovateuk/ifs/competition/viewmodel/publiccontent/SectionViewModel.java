package org.innovateuk.ifs.competition.viewmodel.publiccontent;


/**
 * Single section used to show tabs etc.
 */
public class SectionViewModel {
    private String path;
    private String text;
    private Boolean isActive;

    public SectionViewModel(String path, String text, Boolean isActive) {
        this.path = path;
        this.text = text;
        this.isActive = isActive;
    }

    public String getPath() {
        return path;
    }

    public String getText() {
        return text;
    }

    public Boolean getActive() {
        return isActive;
    }
}
