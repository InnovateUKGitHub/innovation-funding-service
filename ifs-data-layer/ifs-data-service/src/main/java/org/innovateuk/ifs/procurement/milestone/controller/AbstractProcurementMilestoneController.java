package org.innovateuk.ifs.procurement.milestone.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.transactional.ProcurementMilestoneService;
import org.springframework.web.bind.annotation.*;

public abstract class AbstractProcurementMilestoneController<R extends ProcurementMilestoneResource> {

    @PostMapping
    public RestResult<R> create(@RequestBody final R milestone) {
        return getProcurementMilestoneService().create(milestone).toPostCreateResponse();
    }

    @GetMapping("/{id}")
    public RestResult<R> get(@PathVariable final long id) {
        return getProcurementMilestoneService().get(id).toGetResponse();
    }

    @PutMapping("/{id}")
    public RestResult<Void> update(@PathVariable final long id, @RequestBody final R milestone) {
        return getProcurementMilestoneService().update(milestone).toPutResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> delete(@PathVariable final long id) {
        return getProcurementMilestoneService().delete(id).toDeleteResponse();
    }

    protected abstract ProcurementMilestoneService<R> getProcurementMilestoneService();

}
