package org.innovateuk.ifs.setup.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.innovateuk.ifs.setup.mapper.SetupStatusMapper;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;

/**
 * Implements {@link SetupStatusService}
 */
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
        Optional<SetupStatus> resultOpt = setupStatusRepository.findByClassNameAndClassPk(className, classPk);
        if(resultOpt.isPresent()) {
            return ServiceResult.serviceSuccess(setupStatusMapper.mapToResource(resultOpt.get()));
        }
        return ServiceResult.serviceFailure(notFoundError(SetupStatus.class, classPk, className));
    }

    @Override
    public ServiceResult<SetupStatusResource> findSetupStatusAndTarget(String className, Long classPk, String targetClassName, Long targetId) {
        Optional<SetupStatus> resultOpt = setupStatusRepository.findByClassNameAndClassPkAndTargetClassNameAndTargetId(className, classPk, targetClassName, targetId);

        if(resultOpt.isPresent()) {
            return ServiceResult.serviceSuccess(setupStatusMapper.mapToResource(resultOpt.get()));
        }
        return ServiceResult.serviceFailure(notFoundError(SetupStatus.class, classPk, className));
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
