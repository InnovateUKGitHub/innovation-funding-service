package com.worth.ifs.user.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.user.domain.Affiliation;
import com.worth.ifs.user.resource.AffiliationResource;
import org.mapstruct.Mapper;

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

    public abstract Iterable<AffiliationResource> mapToResource(Iterable<Affiliation> domain);

    public abstract Affiliation mapToDomain(AffiliationResource resource);

    public abstract Iterable<Affiliation> mapToDomain(Iterable<AffiliationResource> resource);
}