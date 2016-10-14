package com.worth.ifs.project.finance.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.organisation.mapper.OrganisationMapper;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                OrganisationMapper.class,
                ProjectMapper.class,
                CostCategoryTypeMapper.class,
                CostGroupMapper.class,
                UserMapper.class
        }
)
public abstract class SpendProfileMapper extends BaseMapper<SpendProfile, SpendProfileResource, Long> {
    @Override
    public abstract SpendProfileResource mapToResource(SpendProfile spendProfile);

    @Override
    public abstract SpendProfile mapToDomain(SpendProfileResource spendProfileResource);


    public Long mapSpendProfileToId(SpendProfile object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}