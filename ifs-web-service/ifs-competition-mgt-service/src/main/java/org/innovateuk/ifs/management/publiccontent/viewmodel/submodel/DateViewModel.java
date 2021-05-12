package org.innovateuk.ifs.management.publiccontent.viewmodel.submodel;

import org.innovateuk.ifs.management.publiccontent.viewmodel.DatesViewModel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Repeating viewmodel that's being used by {@link DatesViewModel}
 */
public class DateViewModel {
    private ZonedDateTime dateTime;
    private String content;
    private String nullDateText = "Unknown";

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy");

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeFormatted() {
        if (null == dateTime) {
            return nullDateText;
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

    public String getNullDateText() {
        return nullDateText;
    }

    public DateViewModel setNullDateText(String nullDateText) {
        this.nullDateText = nullDateText;
        return this;
    }
}
