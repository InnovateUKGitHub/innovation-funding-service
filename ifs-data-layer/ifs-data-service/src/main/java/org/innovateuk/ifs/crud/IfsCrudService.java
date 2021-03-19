package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;

public interface IfsCrudService<Resource, Id> {

    ServiceResult<Resource> get(Id id);

    ServiceResult<List<Resource>> get(List<Id> ids);

    ServiceResult<Resource> update(Id id, Resource resource);

    ServiceResult<Void> delete(Id id);

    ServiceResult<Resource> create(Resource resource);

}
