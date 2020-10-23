package org.innovateuk.ifs.supporter.resource;

public class SupporterDashboardApplicationResource {

    private long id;
    private String name;
    private String lead;
    private SupporterState state;

    public SupporterDashboardApplicationResource() {}

    public SupporterDashboardApplicationResource(long id, String name, String lead, SupporterState state) {
        this.id = id;
        this.name = name;
        this.lead = lead;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public SupporterState getState() {
        return state;
    }

    public void setState(SupporterState state) {
        this.state = state;
    }
}
