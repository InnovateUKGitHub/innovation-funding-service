package org.innovateuk.ifs.alert.mapper;

import org.innovateuk.ifs.alert.domain.Alert;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.mapstruct.Mapper;

/**
 * Maps between domain and resource DTO for {@link Alert}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {}
)
public abstract class AlertMapper extends BaseMapper<Alert, AlertResource, Long> {

    public Long mapAlertToId(final Alert object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

}
