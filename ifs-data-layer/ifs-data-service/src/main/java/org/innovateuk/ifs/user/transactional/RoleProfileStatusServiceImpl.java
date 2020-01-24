package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.mapper.RoleProfileStatusMapper;
import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;


@Service
public class RoleProfileStatusServiceImpl implements RoleProfileStatusService {

    @Autowired
    private RoleProfileStatusRepository roleProfileStatusRepository;

    @Autowired
    private RoleProfileStatusMapper roleProfileStatusMapper;

    @Override
    @Transactional
    public ServiceResult<Void> updateUserStatus(long userId, RoleProfileStatusResource roleProfileStatusResource) {
        roleProfileStatusRepository.save(roleProfileStatusMapper.mapToDomain(roleProfileStatusResource));
        return serviceSuccess();
    }
}
