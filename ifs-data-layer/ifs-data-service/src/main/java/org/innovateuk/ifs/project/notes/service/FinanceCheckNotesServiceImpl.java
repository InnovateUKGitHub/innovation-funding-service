package org.innovateuk.ifs.project.notes.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.service.MappingThreadService;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FinanceCheckNotesServiceImpl implements FinanceCheckNotesService {
    private final ThreadService<NoteResource, PostResource> service;

    @Autowired
    public FinanceCheckNotesServiceImpl(NoteRepository noteRepository, AuthenticationHelper authenticationHelper, NoteMapper noteMapper, PostMapper postMapper) {
        service = new MappingThreadService<>(noteRepository, authenticationHelper, noteMapper, postMapper, ProjectFinance.class);
    }

    @Override
    @Transactional
    public ServiceResult<Long> create(NoteResource note) {
        return service.create(note);
    }

    @Override
    public ServiceResult<Void> close(Long noteId) {
        return service.close(noteId);
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
    @Transactional
    public ServiceResult<Void> addPost(PostResource post, Long threadId) {
        return service.addPost(post, threadId);
    }
}