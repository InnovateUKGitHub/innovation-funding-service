package com.worth.ifs.alert.mapper;

import com.worth.ifs.alert.domain.Alert;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
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