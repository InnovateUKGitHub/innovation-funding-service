package org.innovateuk.ifs.competitionsetup.form;

import java.time.ZonedDateTime;

import static java.util.Arrays.stream;

/**
 * Enum for available values for milestone times.
 */
public enum MilestoneTime {

    NINE_AM("9:00 am", 9),
    TEN_AM("10:00 am", 10),
    ELEVEN_AM("11:00 am", 11),
    TWELVE_PM("12:00 pm", 12),
    ONE_PM("1:00 pm", 13),
    TWO_PM("2:00 pm", 14),
    THREE_PM("3:00 pm", 15),
    FOUR_PM("4:00 pm", 16),
    FIVE_PM("5:00 pm", 17),
    SIX_PM("6:00 pm", 18);

    private String display;
    private int hour;

    MilestoneTime(String display, int hour) {
        this.display = display;
        this.hour = hour;
    }

    public String getDisplay() {
        return display;
    }

    public int getHour() {
        return hour;
    }

    public static MilestoneTime fromZonedDateTime(ZonedDateTime dateTime) {
        return stream(MilestoneTime.values())
                .filter(milestoneTime -> milestoneTime.getHour() == dateTime.getHour())
                .findAny()
                .orElse(TWELVE_PM);
    }
}
