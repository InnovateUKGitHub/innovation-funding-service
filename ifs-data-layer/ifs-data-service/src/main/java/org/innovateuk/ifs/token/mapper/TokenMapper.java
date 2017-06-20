package org.innovateuk.ifs.token.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.resource.TokenResource;
import org.mapstruct.Mapper;


@Mapper(
    config = GlobalMapperConfig.class
)
public abstract class TokenMapper extends BaseMapper<Token, TokenResource, Long> {

}
