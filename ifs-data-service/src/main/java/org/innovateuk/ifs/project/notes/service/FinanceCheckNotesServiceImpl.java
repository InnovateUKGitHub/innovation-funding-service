package org.innovateuk.ifs.project.notes.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.service.MappingThreadService;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanceCheckNotesServiceImpl implements FinanceCheckNotesService {
    private final ThreadService<NoteResource, PostResource> service;

    @Autowired
    public FinanceCheckNotesServiceImpl(NoteRepository noteRepository, NoteMapper noteMapper, PostMapper postMapper) {
        service = new MappingThreadService<>(noteRepository, noteMapper, postMapper, ProjectFinance.class);
    }

    @Override
    public ServiceResult<Long> create(NoteResource note) {
        return service.create(note);
    }

    @Override
    public ServiceResult<NoteResource> findOne(Long id) {
        return service.findOne(id);
    }

    @Override
    public ServiceResult<List<NoteResource>> findAll(Long classContextId) {
        return service.findAll(classContextId);
    }

    @Override
    public ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return service.addPost(post, threadId);
    }
}