package org.innovateuk.ifs.management.viewmodel;

public class InterviewAllocatedApplicationRowViewModel {

    private final  long id;
    private final String name;
    private final String leadOrganisation;
    private final long numberOfAssessors;

    public InterviewAllocatedApplicationRowViewModel(long id, String name, String leadOrganisation, long numberOfAssessors) {
        this.id = id;
        this.name = name;
        this.leadOrganisation = leadOrganisation;
        this.numberOfAssessors = numberOfAssessors;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public long getNumberOfAssessors() {
        return numberOfAssessors;
    }
}
