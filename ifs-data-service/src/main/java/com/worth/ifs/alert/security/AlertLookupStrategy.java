package com.worth.ifs.alert.security;

import com.worth.ifs.alert.mapper.AlertMapper;
import com.worth.ifs.alert.repository.AlertRepository;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link AlertResource}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AlertLookupStrategy {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AlertMapper alertMapper;

    @PermissionEntityLookupStrategy
    public AlertResource getAlertResource(final Long id){
        return alertMapper.mapToResource(alertRepository.findOne(id));
    }

}
