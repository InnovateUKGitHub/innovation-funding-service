package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.commons.service.ServiceResult;

public interface IfsCrudService<Resource, Id> {

    ServiceResult<Resource> get(Id id);

    ServiceResult<Resource> update(Id id, Resource resource);

    ServiceResult<Void> delete(Id id);

    ServiceResult<Resource> create(Resource resource);

}
