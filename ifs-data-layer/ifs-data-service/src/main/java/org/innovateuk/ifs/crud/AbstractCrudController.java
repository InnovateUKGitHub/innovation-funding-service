package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

public abstract class AbstractCrudController<R, Id> {

    @Autowired
    private IfsCrudService<R, Id> crudService;

    @PostMapping
    public RestResult<R> create(@RequestBody R resource) {
        return crudService.create(resource).toPostCreateResponse();
    }

    @GetMapping("/{id}")
    public RestResult<R> get(@PathVariable Id id) {
        return crudService.get(id).toGetResponse();
    }

    @PutMapping("/{id}")
    public RestResult<Void> update(@PathVariable Id id, @RequestBody R resource) {
        return crudService.update(id, resource).toPutResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> delete(@PathVariable Id id) {
        return crudService.delete(id).toDeleteResponse();
    }


}
