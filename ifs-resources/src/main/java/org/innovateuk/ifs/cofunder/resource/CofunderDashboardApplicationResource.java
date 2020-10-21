package org.innovateuk.ifs.cofunder.resource;

public class CofunderDashboardApplicationResource {

    private long id;
    private String name;
    private String lead;
    private CofunderState state;

    public CofunderDashboardApplicationResource() {}

    public CofunderDashboardApplicationResource(long id, String name, String lead, CofunderState state) {
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

    public CofunderState getState() {
        return state;
    }

    public void setState(CofunderState state) {
        this.state = state;
    }
}
