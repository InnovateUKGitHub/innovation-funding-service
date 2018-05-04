package org.innovateuk.ifs.interview.resource;

import org.innovateuk.ifs.commons.resource.PageResource;

import java.util.List;

public class InterviewApplicationPageResource extends PageResource<InterviewApplicationResource> {

    private long unallocatedApplications;
    private long allocatedApplications;


    public InterviewApplicationPageResource() {
    }

    public InterviewApplicationPageResource(long totalElements, int totalPages, List<InterviewApplicationResource> content, int number, int size, long unallocatedApplications, long allocatedApplications) {
        super(totalElements, totalPages, content, number, size);
    }

    public long getUnallocatedApplications() {
        return unallocatedApplications;
    }

    public void setUnallocatedApplications(long unallocatedApplications) {
        this.unallocatedApplications = unallocatedApplications;
    }

    public long getAllocatedApplications() {
        return allocatedApplications;
    }

    public void setAllocatedApplications(long allocatedApplications) {
        this.allocatedApplications = allocatedApplications;
    }
}