package org.innovateuk.ifs.finance.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.mapper.OrganisationSizeMapper;
import org.innovateuk.ifs.finance.repository.OrganisationSizeRepository;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Implementation of {@link OrganisationSizeService} for retrieving {@link OrganisationSizeResource} records.
 */
@Service
public class OrganisationSizeServiceImpl extends BaseTransactionalService implements OrganisationSizeService {

    @Autowired
    private OrganisationSizeRepository organisationSizeRepository;

    @Autowired
    private OrganisationSizeMapper organisationSizeMapper;

    @Override
    public ServiceResult<List<OrganisationSizeResource>> getOrganisationSizes() {
        return serviceSuccess(Lists.newArrayList(organisationSizeMapper.mapToResource(organisationSizeRepository.findAll())));
    }
}
