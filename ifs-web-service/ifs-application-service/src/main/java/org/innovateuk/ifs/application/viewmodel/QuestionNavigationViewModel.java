package org.innovateuk.ifs.application.viewmodel;

import org.springframework.util.StringUtils;

/**
 * TODO - comments that make sense
 */
public class QuestionNavigationViewModel {
    private String previousUrl;
    private String previousText;
    private String nextUrl;
    private String nextText;

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
    }

    public String getPreviousText() {
        return previousText;
    }

    public void setPreviousText(String previousText) {
        this.previousText = previousText;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public String getNextText() {
        return nextText;
    }

    public void setNextText(String nextText) {
        this.nextText = nextText;
    }

    public Boolean getHasNavigation(){
        return !StringUtils.isEmpty(previousUrl) && !StringUtils.isEmpty(nextUrl);
    }
}
