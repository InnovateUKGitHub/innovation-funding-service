package org.innovateuk.ifs.finance.resource;

/**
 * Compound class for holding the application finance resource keys
 */
public class ApplicationFinanceResourceId extends BaseFinanceResourceId {

    public ApplicationFinanceResourceId(Long applicationId, Long organisationId) {
        super(applicationId, organisationId);
    }

    public Long getApplicationId() {
        return super.getTargetId();
    }
}
