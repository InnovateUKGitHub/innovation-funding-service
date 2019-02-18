package org.innovateuk.ifs.euactiontype.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.euactiontype.mapper.EuActionTypeMapper;
import org.innovateuk.ifs.euactiontype.repository.EuActionTypeRepository;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;
import org.innovateuk.ifs.euactiontype.domain.EuActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class EuActionTypeServiceImpl implements EuActionTypeService{

    @Autowired
    private EuActionTypeRepository euActionTypeRepository;

    @Autowired
    private EuActionTypeMapper euActionTypeMapper;

    @Override
    public ServiceResult<List<EuActionTypeResource>> findAll() {
        return serviceSuccess(euActionTypeRepository.findAllByOrderByPriorityAsc().stream()
                .map(euActionTypeMapper::mapToResource)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<EuActionTypeResource> getById(long id) {

        return find(euActionTypeRepository.findById(id), notFoundError(EuActionType.class, id))
                .andOnSuccessReturn(euActionTypeMapper::mapToResource);

    }
}
