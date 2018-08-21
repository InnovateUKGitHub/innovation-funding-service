package org.innovateuk.ifs.eugrant.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.mapper.EuGrantMapper;
import org.innovateuk.ifs.eugrant.repository.EuGrantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class EuGrantServiceImpl implements EuGrantService {

    @Autowired
    private EuGrantMapper euGrantMapper;

    @Autowired
    private EuGrantRepository euGrantRepository;

    @Override
    public ServiceResult<EuGrantResource> save(EuGrantResource euGrant) {
        return serviceSuccess(euGrantMapper.mapToResource(
                euGrantRepository.save(
                        euGrantMapper.mapToDomain(euGrant))));
    }

    @Override
    public ServiceResult<EuGrantResource> findById(UUID id) {
        return serviceSuccess(euGrantMapper.mapToResource(
                euGrantRepository.findOne(id)));
    }
}
