package org.innovateuk.ifs.setup.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.setup.domain.SetupStatus;
import org.innovateuk.ifs.setup.mapper.SetupStatusMapper;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.innovateuk.ifs.setup.resource.SetupStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;

@Service
public class SetupStatusServiceImpl implements SetupStatusService {

    @Autowired
    private SetupStatusRepository setupStatusRepository;

    @Autowired
    private SetupStatusMapper setupStatusMapper;

    @Override
    public ServiceResult<Iterable<SetupStatusResource>> findByTargetIdAndTargetClassName(Long targetId, String targetClassName) {
        return ServiceResult.serviceSuccess(setupStatusMapper
                .mapToResource(setupStatusRepository
                        .findByTargetIdAndTargetClassName(targetId, targetClassName)));
    }

    @Override
    public ServiceResult<Iterable<SetupStatusResource>> findByTargetClassNameAndTargetIdAndParentId(String targetClassName, Long targetId, Long parentId) {
        return ServiceResult.serviceSuccess(setupStatusMapper.mapToResource(setupStatusRepository.findByTargetClassNameAndTargetIdAndParentId(targetClassName, targetId, parentId)));
    }

    @Override
    public ServiceResult<Iterable<SetupStatusResource>> findByTargetClassNameAndParentId(String targetClassName, Long parentId) {
        return ServiceResult.serviceSuccess(setupStatusMapper.mapToResource(setupStatusRepository.findByTargetClassNameAndParentId(targetClassName, parentId)));
    }

    @Override
    public ServiceResult<SetupStatusResource> findSetupStatus(Long classPk, String className) {
        Optional<SetupStatus> setupStatusOpt = setupStatusRepository.findByClassPkAndClassName(classPk, className);
        if(setupStatusOpt.isPresent()) {
            return ServiceResult.serviceSuccess(setupStatusMapper.mapToResource(setupStatusOpt.get()));
        } else {
            return ServiceResult.serviceFailure(notFoundError(SetupStatus.class, classPk, className));
        }
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
