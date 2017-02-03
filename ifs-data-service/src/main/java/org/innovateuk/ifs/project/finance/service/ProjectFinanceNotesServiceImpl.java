package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.mapper.NoteMapper;
import org.innovateuk.ifs.threads.mapper.PostMapper;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.service.MappingThreadService;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectFinanceNotesServiceImpl extends MappingThreadService<Note, NoteResource, NoteMapper, ProjectFinance>
        implements ProjectFinanceNotesService {

    @Autowired
    public ProjectFinanceNotesServiceImpl(NoteRepository noteRepository, NoteMapper queryMapper, PostMapper postMapper) {
        super(noteRepository, queryMapper, postMapper, ProjectFinance.class);
    }
}