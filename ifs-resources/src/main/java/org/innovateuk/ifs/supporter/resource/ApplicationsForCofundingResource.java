package org.innovateuk.ifs.supporter.resource;

public class ApplicationsForCofundingResource {

    private long id;
    private String name;
    private String lead;

    private long rejected;
    private long accepted;
    private long assigned;

    public ApplicationsForCofundingResource() {}
    public ApplicationsForCofundingResource(long id, String name, String lead, long rejected, long accepted, long assigned) {
        this.id = id;
        this.name = name;
        this.lead = lead;
        this.rejected = rejected;
        this.accepted = accepted;
        this.assigned = assigned;
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

    public long getTotal() {
        return rejected + accepted + assigned;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getAssigned() {
        return assigned;
    }

    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }
}
