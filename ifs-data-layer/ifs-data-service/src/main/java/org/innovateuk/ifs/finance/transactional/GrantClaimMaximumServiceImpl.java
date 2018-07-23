package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class GrantClaimMaximumServiceImpl implements GrantClaimMaximumService {

    private GrantClaimMaximumRepository repository;
    private GrantClaimMaximumMapper grantClaimMaximumMapper;

    public GrantClaimMaximumServiceImpl(GrantClaimMaximumRepository repository,
                                        GrantClaimMaximumMapper grantClaimMaximumMapper) {
        this.repository = repository;
        this.grantClaimMaximumMapper = grantClaimMaximumMapper;
    }

    @Override
    public ServiceResult<GrantClaimMaximumResource> getGrantClaimMaximumById(Long id) {
        return find(repository.findOne(id), notFoundError(GrantClaimMaximum.class, id)).andOnSuccess(
                maximum -> serviceSuccess(grantClaimMaximumMapper.mapToResource(maximum)));
    }

    @Override
    public ServiceResult<Void> update(GrantClaimMaximumResource grantClaimMaximumResource) {
        GrantClaimMaximum max = grantClaimMaximumMapper.mapIdToDomain(grantClaimMaximumResource.getId());
        max.setMaximum(grantClaimMaximumResource.getMaximum());
        repository.save(max);
        return serviceSuccess();
    }
}
