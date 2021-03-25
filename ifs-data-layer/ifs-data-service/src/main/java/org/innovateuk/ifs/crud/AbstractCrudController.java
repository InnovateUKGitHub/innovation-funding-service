package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class AbstractCrudController<R, Id> {

    @PostMapping
    public RestResult<R> create(@RequestBody R resource) {
        return crudService().create(resource).toPostCreateResponse();
    }

    @GetMapping
    public RestResult<List<R>> get(@RequestParam List<Id> ids) {
        return crudService().get(ids).toGetResponse();
    }

    @GetMapping("/{id}")
    public RestResult<R> get(@PathVariable Id id) {
        return crudService().get(id).toGetResponse();
    }

    @PutMapping("/{id}")
    public RestResult<Void> update(@PathVariable Id id, @RequestBody R resource) {
        return crudService().update(id, resource).toPutResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> delete(@PathVariable Id id) {
        return crudService().delete(id).toDeleteResponse();
    }

    protected abstract IfsCrudService<R, Id> crudService();

}
