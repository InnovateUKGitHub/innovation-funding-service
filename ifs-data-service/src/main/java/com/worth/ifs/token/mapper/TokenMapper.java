package com.worth.ifs.token.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.resource.TokenResource;
import org.mapstruct.Mapper;


@Mapper(
    config = GlobalMapperConfig.class
)
public abstract class TokenMapper extends BaseMapper<Token, TokenResource, Long> {

}
