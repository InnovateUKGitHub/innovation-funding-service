package org.innovateuk.ifs.threads.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.threads.resource.PostResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = { UserMapper.class, FileEntryMapper.class }
)
public abstract class PostMapper extends BaseMapper<Post, PostResource, Long> {}