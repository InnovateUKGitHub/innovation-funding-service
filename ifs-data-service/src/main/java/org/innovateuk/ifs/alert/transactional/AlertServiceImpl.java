package org.innovateuk.ifs.alert.transactional;

import org.innovateuk.ifs.alert.domain.Alert;
import org.innovateuk.ifs.alert.mapper.AlertMapper;
import org.innovateuk.ifs.alert.repository.AlertRepository;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static java.time.ZonedDateTime.now;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.alert.domain.Alert} data.
 */
@Service
public class AlertServiceImpl extends BaseTransactionalService implements AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertMapper alertMapper;

    @Override
    public ServiceResult<List<AlertResource>> findAllVisible() {
        return serviceSuccess(simpleMap(alertRepository.findAllVisible(now()), alertMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<AlertResource>> findAllVisibleByType(AlertType type) {
        return serviceSuccess(simpleMap(alertRepository.findAllVisibleByType(type, now()), alertMapper::mapToResource));
    }

    @Override
    public ServiceResult<AlertResource> findById(Long id) {
        return find(alertRepository.findOne(id), notFoundError(Alert.class, id)).andOnSuccessReturn(alertMapper::mapToResource);
    }

    @Override
    public ServiceResult<AlertResource> create(AlertResource alertResource) {
        Alert saved = alertRepository.save(alertMapper.mapToDomain(alertResource));
        return serviceSuccess(alertMapper.mapToResource(saved));
    }

    @Override
    public ServiceResult<Void> delete(Long id) {
        alertRepository.delete(id);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> deleteAllByType(AlertType type) {
        alertRepository.deleteByType(type);
        return serviceSuccess();
    }
}
