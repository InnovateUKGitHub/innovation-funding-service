package org.innovateuk.ifs.organisation.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationTypeRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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
