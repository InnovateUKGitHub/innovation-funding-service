package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

public interface IfsCrudRestService<Resource, Id> {

    RestResult<Resource> get(Id id);

    RestResult<List<Resource>> get(List<Id> ids);

    RestResult<Void> update(Id id, Resource resource);

    RestResult<Void> delete(Id id);

    RestResult<Resource> create(Resource resource);
}