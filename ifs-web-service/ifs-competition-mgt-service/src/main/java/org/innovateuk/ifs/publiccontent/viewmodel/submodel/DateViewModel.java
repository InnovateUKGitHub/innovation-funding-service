package org.innovateuk.ifs.publiccontent.viewmodel.submodel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Repeating viewmodel that's being used by {@link org.innovateuk.ifs.publiccontent.viewmodel.DatesViewModel}
 */
public class DateViewModel {
    private LocalDateTime dateTime;
    private String content;

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM YYYY");

    public String getDateTime() {
        if(null == dateTime) {
            return "Unknown";
        }

        return dateTime.format(DATE_FORMAT);
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
