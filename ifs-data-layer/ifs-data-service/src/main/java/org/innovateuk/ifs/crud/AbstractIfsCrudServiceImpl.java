package org.innovateuk.ifs.crud;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public abstract class AbstractIfsCrudServiceImpl<Resource, Domain, Id> implements IfsCrudService<Resource, Id> {

    @Autowired
    private CrudRepository<Domain, Id> crudRepository;

    @Autowired
    private BaseResourceMapper<Domain, Resource> mapper;

    @Override
    public ServiceResult<Resource> get(Id id) {
        return findById(id)
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<Resource> update(Id id, Resource resource) {
        return findById(id)
                .andOnSuccess(domain -> {
                    mapToDomain(domain, resource);
                    return serviceSuccess(crudRepository.save(domain))
                            .andOnSuccessReturn(mapper::mapToResource);
                });
    }

    @Override
    public ServiceResult<Void> delete(Id id) {
        crudRepository.deleteById(id);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Resource> create(Resource resource) {
        Domain domain = null;
        try {
            domain = getDomainClazz().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            //todod log.
        }
        mapToDomain(domain, resource);
        return serviceSuccess(crudRepository.save(domain))
                .andOnSuccessReturn(mapper::mapToResource);
    }


    private ServiceResult<Domain> findById(Id id) {
        return find(crudRepository.findById(id), notFoundError(getDomainClazz(), id));
    }

    protected abstract Class<Domain> getDomainClazz();

    protected abstract Domain mapToDomain(Domain domain, Resource resource);
}
