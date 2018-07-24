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

    private GrantClaimMaximumRepository grantClaimMaximumRepository;
    private GrantClaimMaximumMapper grantClaimMaximumMapper;

    public GrantClaimMaximumServiceImpl(GrantClaimMaximumRepository grantClaimMaximumRepository,
                                        GrantClaimMaximumMapper grantClaimMaximumMapper) {
        this.grantClaimMaximumRepository = grantClaimMaximumRepository;
        this.grantClaimMaximumMapper = grantClaimMaximumMapper;
    }

    @Override
    public ServiceResult<GrantClaimMaximumResource> getGrantClaimMaximumById(Long id) {
        return find(grantClaimMaximumRepository.findOne(id), notFoundError(GrantClaimMaximum.class, id)).andOnSuccess(
                maximum -> serviceSuccess(grantClaimMaximumMapper.mapToResource(maximum)));
    }

    @Override
    public ServiceResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource) {
        GrantClaimMaximum gcm = grantClaimMaximumRepository.save(grantClaimMaximumMapper.mapToDomain(grantClaimMaximumResource));
        return serviceSuccess(grantClaimMaximumMapper.mapToResource(gcm));
    }
}
