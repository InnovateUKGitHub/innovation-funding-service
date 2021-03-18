package org.innovateuk.ifs.crud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

public abstract class AbstractIfsCrudServiceImpl<Resource, Domain, Id> extends RootTransactionalService implements IfsCrudService<Resource, Id> {
    private final Log LOG = LogFactory.getLog(this.getClass());

    protected abstract CrudRepository<Domain, Id> crudRepository();

    @Autowired
    protected BaseResourceMapper<Domain, Resource> mapper;

    @Override
    public ServiceResult<Resource> get(Id id) {
        return findById(id)
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<List<Resource>> get(List<Id> ids) {
        return serviceSuccess(
                stream(crudRepository().findAllById(ids).spliterator(), false)
                .map(mapper::mapToResource)
                .collect(toList())
        );
    }


    @Override
    @Transactional
    public ServiceResult<Resource> update(Id id, Resource resource) {
        return findById(id)
                .andOnSuccess(domain -> {
                    mapToDomain(domain, resource);
                    return serviceSuccess(crudRepository().save(domain))
                            .andOnSuccessReturn(mapper::mapToResource);
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> delete(Id id) {
        crudRepository().deleteById(id);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Resource> create(Resource resource) {
        Domain domain = null;
        try {
            domain = getDomainClazz().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error(e);
        }
        mapToDomain(domain, resource);
        return serviceSuccess(crudRepository().save(domain))
                .andOnSuccessReturn(mapper::mapToResource);
    }


    private ServiceResult<Domain> findById(Id id) {
        return find(crudRepository().findById(id), notFoundError(getDomainClazz(), id));
    }

    protected abstract Class<Domain> getDomainClazz();

    protected abstract Domain mapToDomain(Domain domain, Resource resource);
}
