package org.innovateuk.ifs.application.resource;

/**
 * Contains the information required when sending an email to notify of an application funding decision.
 */
public class ApplicationDecisionToSendApplicationResource {
    private long id;
    private String title;
    private String lead;
    private Decision decision;

    private ApplicationDecisionToSendApplicationResource() {}

    public ApplicationDecisionToSendApplicationResource(long id, String title, String lead, Decision decision) {
        this.id = id;
        this.title = title;
        this.lead = lead;
        this.decision = decision;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLead() {
        return lead;
    }

    public Decision getDecision() {
        return decision;
    }
}