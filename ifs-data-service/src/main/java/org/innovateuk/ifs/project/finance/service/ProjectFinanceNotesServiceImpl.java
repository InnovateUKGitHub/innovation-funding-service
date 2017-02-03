package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.service.GenericThreadService;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class ProjectFinanceNotesServiceImpl implements ProjectFinanceNotesService {
    private final GenericThreadService<Note, Post, ProjectFinance> service;
    private final NoteMapper noteMapper;
    private final PostMapper postMapper;

    @Autowired
    public ProjectFinanceNotesServiceImpl(NoteRepository noteRepository, NoteMapper noteMapper, PostMapper postMapper) {
        this.service = new GenericThreadService<>(noteRepository, ProjectFinance.class);
        this.noteMapper = noteMapper;
        this.postMapper = postMapper;
    }

    public final ServiceResult<List<NoteResource>> findAll(Long projectFinanceId) {
        return service.findAll(projectFinanceId)
                .andOnSuccessReturn(notes -> simpleMap(notes, noteMapper::mapToResource));
    }

    public final ServiceResult<NoteResource> findOne(Long contextClassPk) {
        return service.findOne(contextClassPk).andOnSuccessReturn(noteMapper::mapToResource);
    }

    public final ServiceResult<Void> create(NoteResource note) {
        return service.create(noteMapper.mapToDomain(note));
    }

    public final ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return service.addPost(postMapper.mapToDomain(post), threadId);
    }

}