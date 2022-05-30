package org.innovateuk.ifs.competition.viewmodel;

import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.competition.status.PublicContentStatusText;

import java.time.ZonedDateTime;

/**
 * View model for Competition Public Content Search items.
 */
@Setter
@Getter
public class PublicContentItemViewModel {

    private String shortDescription;
    private String eligibilitySummary;
    private String competitionTitle;
    private Long competitionId;
    private ZonedDateTime competitionOpenDate;
    private ZonedDateTime competitionCloseDate;
    private ZonedDateTime registrationCloseDate;
    private PublicContentStatusText publicContentStatusText;
    private boolean alwaysOpen;
    private String hash;
    private boolean eoiEnabled;

    public String getNullCloseDateText() {
        return alwaysOpen ? "No submission deadline" : "Unknown";
    }
}
