package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class NoteLookupStrategy {

    @Autowired
    private NoteRepository repository;

    @Autowired
    private NoteMapper mapper;

    @PermissionEntityLookupStrategy
    public NoteResource findById(final Long noteId) {
        return mapper.mapToResource(repository.findOne(noteId));
    }

}