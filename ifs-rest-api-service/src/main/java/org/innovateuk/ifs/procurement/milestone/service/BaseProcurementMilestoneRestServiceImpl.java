package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;

public abstract class BaseProcurementMilestoneRestServiceImpl<R extends ProcurementMilestoneResource> extends BaseRestService implements ProcurementMilestoneRestService<R> {

    private String milestoneUrl;

    protected BaseProcurementMilestoneRestServiceImpl(String milestoneUrl) {
        this.milestoneUrl = milestoneUrl;
    }

    @Override
    public RestResult<R> get(long milestoneId) {
        return getWithRestResult(milestoneUrl + "/" + milestoneId,
                getResourceClass());
    }

    @Override
    public RestResult<R> create(R milestone) {
        return postWithRestResult(milestoneUrl, milestone,
                getResourceClass());
    }

    @Override
    public RestResult<Void> update(R milestone) {
        return putWithRestResult(milestoneUrl + "/" + milestone.getId(), milestone, Void.class);
    }

    @Override
    public RestResult<Void> delete(long milestoneId) {
        return deleteWithRestResult(milestoneUrl + "/" + milestoneId);
    }

    protected String getMilestoneUrl() {
        return milestoneUrl;
    }

    protected abstract Class<R> getResourceClass();

}
