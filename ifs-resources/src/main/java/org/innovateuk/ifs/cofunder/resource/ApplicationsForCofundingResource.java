package org.innovateuk.ifs.cofunder.resource;

public class ApplicationsForCofundingResource {

    private long id;
    private String name;
    private String lead;

    private int total;
    private int rejected;
    private int accepted;
    private int assigned;

    public ApplicationsForCofundingResource(long id, String name, String lead, int total, int rejected, int accepted, int assigned) {
        this.id = id;
        this.name = name;
        this.lead = lead;
        this.total = total;
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRejected() {
        return rejected;
    }

    public void setRejected(int rejected) {
        this.rejected = rejected;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public int getAssigned() {
        return assigned;
    }

    public void setAssigned(int assigned) {
        this.assigned = assigned;
    }
}
