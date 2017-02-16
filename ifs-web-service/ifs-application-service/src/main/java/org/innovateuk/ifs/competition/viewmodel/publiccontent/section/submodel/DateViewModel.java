package org.innovateuk.ifs.competition.viewmodel.publiccontent.section.submodel;

import org.innovateuk.ifs.competition.viewmodel.publiccontent.section.DatesViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Repeating viewmodel that's being used by {@link DatesViewModel}
 */
public class DateViewModel {
    private LocalDateTime dateTime;
    private String content;
    private Boolean mustBeStrong;

    public DateViewModel() {
    }

    public DateViewModel(LocalDateTime dateTime, String content, Boolean mustBeStrong) {
        this.dateTime = dateTime;
        this.content = content;
        this.mustBeStrong = mustBeStrong;
    }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM YYYY");

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeFormatted() {
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

    public Boolean getMustBeStrong() {
        return mustBeStrong;
    }

    public void setMustBeStrong(Boolean mustBeStrong) {
        this.mustBeStrong = mustBeStrong;
    }
}
