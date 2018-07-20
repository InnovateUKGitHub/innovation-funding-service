package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class GrantClaimMaximumMapper {

    @Autowired
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    public abstract Iterable<GrantClaimMaximum> mapToDomain(Iterable<Long> value);

    @Mappings({
            @Mapping(source = "researchCategory.id", target = "researchCategory"),
            @Mapping(source = "organisationType.id", target = "organisationType")
    })
    public abstract GrantClaimMaximumResource mapToResource(GrantClaimMaximum grantClaimMaximum);

    public GrantClaimMaximum mapIdToDomain(Long id) {
        if (id == null) {
            return null;
        }
        return grantClaimMaximumRepository.findOne(id);
    }

    public Long grantClaimMaximumToId(GrantClaimMaximum object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
