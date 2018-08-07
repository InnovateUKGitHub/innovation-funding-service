package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.category.mapper.ResearchCategoryMapper;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.organisation.mapper.OrganisationTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ResearchCategoryMapper.class,
                OrganisationTypeMapper.class,
                CompetitionMapper.class
        }
)
public abstract class GrantClaimMaximumMapper extends BaseMapper<GrantClaimMaximum, GrantClaimMaximumResource, Long> {

    @Autowired
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @Mappings({
            @Mapping(source = "organisationType.id", target = "organisationType"),
    })
    public abstract GrantClaimMaximum mapToDomain(GrantClaimMaximumResource grantClaimMaximumResource);

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
