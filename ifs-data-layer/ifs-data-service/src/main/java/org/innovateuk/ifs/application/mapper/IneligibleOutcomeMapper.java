package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * Maps between domain and resource DTO for {@link IneligibleOutcome}.
 */
@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                UserMapper.class
        }
)
public abstract class IneligibleOutcomeMapper {

    public IneligibleOutcomeResource mapLastInList(List<IneligibleOutcome> object) {
        if (object == null) {
            return null;
        }

        return object.stream()
                .reduce((ineligibleOutcome, ineligibleOutcome2) -> ineligibleOutcome2)
                .map(this::mapToResource).orElse(null);
    }

    @Mappings({
            @Mapping(source = "createdBy.name", target = "removedBy"),
            @Mapping(source = "createdOn", target = "removedOn")
    })
    public abstract IneligibleOutcomeResource mapToResource(IneligibleOutcome domain);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "process", ignore = true)
    })
    public abstract IneligibleOutcome mapToDomain(IneligibleOutcomeResource resource);

}