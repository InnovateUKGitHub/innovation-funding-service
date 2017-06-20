package org.innovateuk.ifs.threads.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.threads.attachments.mapper.AttachmentMapper;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.threads.resource.PostResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = { UserMapper.class, FileEntryMapper.class }
)
public abstract class PostMapper extends BaseMapper<Post, PostResource, Long> {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AttachmentMapper attachmentMapper;

    @Override
    public PostResource mapToResource(Post post) {
        return new PostResource(post.id(), userMapper.mapToResource(post.author()), post.body(),
                simpleMap(post.attachments(), attachmentMapper::mapToResource), post.createdOn());
    }

    @Override
    public Iterable<PostResource> mapToResource(Iterable<Post> arg0) {
        if ( arg0 == null ) {
            return new ArrayList<PostResource>();
        }

        ArrayList<PostResource> iterable = new ArrayList<PostResource>();
        for ( Post post : arg0 ) {
            iterable.add( mapToResource( post ) );
        }

        return iterable;
    }

    @Override
    public Post mapToDomain(PostResource postResource) {
        return new Post(postResource.id, userMapper.mapToDomain(postResource.author),
                postResource.body, simpleMap(postResource.attachments, attachmentMapper::mapToDomain), postResource.createdOn);
    }

    @Override
    public Iterable<Post> mapToDomain(Iterable<PostResource> arg0) {
        if ( arg0 == null ) {
            return new ArrayList<Post>();
        }

        ArrayList<Post> iterable = new ArrayList<Post>();
        for ( PostResource postResource : arg0 ) {
            iterable.add( mapToDomain( postResource ) );
        }

        return iterable;
    }
}