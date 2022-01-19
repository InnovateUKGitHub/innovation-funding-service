package org.innovateuk.ifs.finance.resource;

/**
 * Compound class for holding the project finance resource keys
 */
public class ProjectFinanceResourceId extends BaseFinanceResourceId {

    public ProjectFinanceResourceId(long projectId, long organisationId) {
        super(projectId, organisationId);
    }

    public Long getProjectId() {
        return super.getTargetId();
    }
}
