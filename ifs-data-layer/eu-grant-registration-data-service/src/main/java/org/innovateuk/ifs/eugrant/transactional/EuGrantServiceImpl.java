package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.mapper.EuGrantMapper;
import org.innovateuk.ifs.eugrant.repository.EuGrantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class EuGrantServiceImpl implements EuGrantService {

    @Autowired
    private EuGrantMapper euGrantMapper;

    @Autowired
    private EuGrantRepository euGrantRepository;

    @Override
    public ServiceResult<Void> save(EuGrantResource euGrant) {
        euGrantRepository.save(
                euGrantMapper.mapToDomain(euGrant));
        return serviceSuccess();
    }

    @Override
    public ServiceResult<EuGrantResource> findById(UUID id) {
        return find(euGrantRepository.findById(id), notFoundError(EuGrant.class, id))
                .andOnSuccessReturn(euGrantMapper::mapToResource);
    }

    @Override
    public ServiceResult<EuGrantResource> create() {
        return serviceSuccess(euGrantMapper.mapToResource(euGrantRepository.save(new EuGrant())));
    }
}
