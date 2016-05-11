package com.worth.ifs.alert.transactional;

import com.worth.ifs.alert.domain.Alert;
import com.worth.ifs.alert.domain.AlertType;
import com.worth.ifs.alert.mapper.AlertMapper;
import com.worth.ifs.alert.repository.AlertRepository;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.alert.domain.Alert} data.
 */
@Service
public class AlertServiceImpl extends BaseTransactionalService implements AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertMapper alertMapper;

    @Override
    public ServiceResult<List<AlertResource>> findAllVisible() {
        return serviceSuccess(simpleMap(alertRepository.findAllVisible(), alertMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<AlertResource>> findAllVisibleByType(final AlertType type) {
        return serviceSuccess(simpleMap(alertRepository.findAllVisibleByType(type), alertMapper::mapToResource));
    }

    @Override
    public ServiceResult<AlertResource> findById(final Long id) {
        return find(alertRepository.findOne(id), notFoundError(Alert.class, id)).andOnSuccessReturn(alertMapper::mapToResource);
    }

    @Override
    public ServiceResult<AlertResource> create(final AlertResource alertResource) {
        final Alert saved = alertRepository.save(alertMapper.mapToDomain(alertResource));
        return serviceSuccess(alertMapper.mapToResource(saved));
    }

    @Override
    public ServiceResult<Void> delete(final Long id) {
        alertRepository.delete(id);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> deleteAllByType(final AlertType type) {
        alertRepository.deleteByType(type);
        return serviceSuccess();
    }
}
