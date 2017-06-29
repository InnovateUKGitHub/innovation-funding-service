package org.innovateuk.ifs.threads.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.threads.resource.NoteResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {PostMapper.class}
)
public abstract class NoteMapper extends BaseMapper<Note, NoteResource, Long> {
    @Autowired
    private PostMapper postMapper;

    @Override
    public NoteResource mapToResource(Note note) {
        return new NoteResource(note.id(), note.contextClassPk(), simpleMap(note.posts(), postMapper::mapToResource),
                note.title(), note.createdOn());
    }

    @Override
    public Note mapToDomain(NoteResource noteResource) {
        return new Note(noteResource.id, noteResource.contextClassPk, simpleMap(noteResource.posts, postMapper::mapToDomain),
                noteResource.title, noteResource.createdOn);
    }
}