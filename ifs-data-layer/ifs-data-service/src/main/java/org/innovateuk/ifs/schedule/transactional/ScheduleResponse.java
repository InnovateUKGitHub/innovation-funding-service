package org.innovateuk.ifs.schedule.transactional;

public class ScheduleResponse {
    private String response;

    public ScheduleResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public static ScheduleResponse noWorkNeeded() {
        return new ScheduleResponse(null);
    }
}
