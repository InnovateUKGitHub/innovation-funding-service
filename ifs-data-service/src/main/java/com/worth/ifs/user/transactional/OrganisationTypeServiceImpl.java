package com.worth.ifs.user.transactional;

import com.google.common.collect.Lists;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.mapper.OrganisationTypeMapper;
import com.worth.ifs.user.repository.OrganisationTypeRepository;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class OrganisationTypeServiceImpl extends BaseTransactionalService implements OrganisationTypeService {

    @Autowired
    private OrganisationTypeRepository repository;

    @Autowired
    private OrganisationTypeMapper mapper;

    @Override
    public ServiceResult<OrganisationTypeResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(OrganisationType.class)).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<List<OrganisationTypeResource>> findAll() {
        Iterable<OrganisationType> organisationTypes = repository.findAll();
        List<OrganisationType> organisationTypesList = Lists.newArrayList(organisationTypes);
        return ServiceResult.serviceSuccess(simpleMap(organisationTypesList, mapper::mapToResource));
    }
}