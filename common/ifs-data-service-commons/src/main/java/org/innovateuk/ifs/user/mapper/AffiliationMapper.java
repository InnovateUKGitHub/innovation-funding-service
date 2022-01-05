package org.innovateuk.ifs.user.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Maps between domain and resource DTO for {@link Affiliation}.
 */
@Mapper(
        componentModel = "spring",
        config = GlobalMapperConfig.class,
        uses = {
                UserMapper.class
        }
)
public abstract class AffiliationMapper {

    public Long mapAffiliationToId(Affiliation object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public abstract AffiliationResource mapToResource(Affiliation domain);

    public abstract List<AffiliationResource> mapToResource(Iterable<Affiliation> domain);

    public abstract Affiliation mapToDomain(AffiliationResource resource);

    public abstract Iterable<Affiliation> mapToDomain(Iterable<AffiliationResource> resource);
}
