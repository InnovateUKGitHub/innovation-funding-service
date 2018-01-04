package org.innovateuk.ifs.assessment.panel.resource;

public class AssessmentReviewResource {

    private long id;
    private Long processRole;
    private String event;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getProcessRole() {
        return processRole;
    }

    public void setProcessRole(Long processRole) {
        this.processRole = processRole;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}