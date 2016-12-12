package org.innovateuk.ifs.alert.security;

import org.innovateuk.ifs.alert.mapper.AlertMapper;
import org.innovateuk.ifs.alert.repository.AlertRepository;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
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
