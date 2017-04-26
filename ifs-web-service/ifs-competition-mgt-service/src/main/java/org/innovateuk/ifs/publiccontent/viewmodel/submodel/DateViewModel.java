package org.innovateuk.ifs.publiccontent.viewmodel.submodel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Repeating viewmodel that's being used by {@link org.innovateuk.ifs.publiccontent.viewmodel.DatesViewModel}
 */
public class DateViewModel {
    private ZonedDateTime dateTime;
    private String content;

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM YYYY");

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeFormatted() {
        if(null == dateTime) {
            return "Unknown";
        }

        return dateTime.format(DATE_FORMAT);
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
