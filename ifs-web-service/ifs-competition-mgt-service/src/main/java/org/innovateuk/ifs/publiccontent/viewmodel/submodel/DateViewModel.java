package org.innovateuk.ifs.publiccontent.viewmodel.submodel;

import java.time.LocalDateTime;

/**
 * Repeating viewmodel that's being used by {@link org.innovateuk.ifs.publiccontent.viewmodel.DatesViewModel}
 */
public class DateViewModel {
    private LocalDateTime dateTime;
    private String content;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
