package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.TermsAndConditions;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class TermsAndConditionsMapper extends BaseMapper<TermsAndConditions, TermsAndConditionsResource, Long> {
    public abstract TermsAndConditionsResource mapToResource(TermsAndConditions domain);
}
