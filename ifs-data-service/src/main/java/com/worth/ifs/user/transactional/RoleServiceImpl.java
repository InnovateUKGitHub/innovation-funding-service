package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.mapper.RoleMapper;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.user.domain.Role} data.
 */
@Service
public class RoleServiceImpl extends BaseTransactionalService implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ServiceResult<RoleResource> findByUserRoleType(UserRoleType userRoleType) {
        return find(roleRepository.findOneByName(userRoleType.name()), notFoundError(Role.class, userRoleType.name())).andOnSuccessReturn(roleMapper::mapToResource);
    }
}