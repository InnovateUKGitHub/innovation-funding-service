package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectFinanceNotesServiceImpl extends ThreadService<Note, ProjectFinance> {

    @Autowired
    public ProjectFinanceNotesServiceImpl(NoteRepository noteRepository) {
        super(noteRepository, ProjectFinance.class);
    }
}