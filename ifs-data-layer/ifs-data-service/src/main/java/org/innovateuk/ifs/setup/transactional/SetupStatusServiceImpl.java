package org.innovateuk.ifs.setup.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.innovateuk.ifs.setup.mapper.SetupStatusMapper;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupStatusServiceImpl implements SetupStatusService {

    @Autowired
    private SetupStatusRepository setupStatusRepository;

    @Autowired
    private SetupStatusMapper setupStatusMapper;

    @Override
    public ServiceResult<List<SetupStatusResource>> findByTargetClassNameAndTargetId(String targetClassName, Long targetId) {
        return ServiceResult.serviceSuccess((List<SetupStatusResource>) setupStatusMapper
                .mapToResource(setupStatusRepository
                        .findByTargetClassNameAndTargetId(targetClassName, targetId)));
    }

    @Override
    public ServiceResult<List<SetupStatusResource>> findByTargetClassNameAndTargetIdAndParentId(String targetClassName, Long targetId, Long parentId) {
        return ServiceResult.serviceSuccess((List<SetupStatusResource>) setupStatusMapper
                .mapToResource(setupStatusRepository
                        .findByTargetClassNameAndTargetIdAndParentId(targetClassName, targetId, parentId)));
    }

    @Override
    public ServiceResult<List<SetupStatusResource>> findByClassNameAndParentId(String className, Long parentId) {
        return ServiceResult.serviceSuccess((List<SetupStatusResource>) setupStatusMapper
                .mapToResource(setupStatusRepository
                        .findByClassNameAndParentId(className, parentId)));
    }

    @Override
    public ServiceResult<SetupStatusResource> findSetupStatus(String className, Long classPk) {
        return ServiceResult.serviceSuccess(setupStatusMapper.mapToResource(setupStatusRepository.findByClassNameAndClassPk(className, classPk)));
    }

    @Override
    public ServiceResult<SetupStatusResource> findSetupStatusAndTarget(String className, Long classPk, String targetClassName, Long targetId) {
        return ServiceResult.serviceSuccess(setupStatusMapper.mapToResource(setupStatusRepository.findByClassNameAndClassPkAndTargetClassNameAndTargetId(className, classPk, targetClassName, targetId)));
    }

    @Override
    public ServiceResult<SetupStatusResource> saveSetupStatus(SetupStatusResource setupStatusResource) {
        SetupStatus setupStatusToSave = setupStatusMapper.mapToDomain(setupStatusResource);

        return ServiceResult.serviceSuccess(
                setupStatusMapper
                        .mapToResource(setupStatusRepository
                                .save(setupStatusToSave)));
    }
}
