package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.thread.service.ThreadRestService;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

public class ProjectFinanceNotesRestServiceImpl extends ThreadRestService<NoteResource> {

    public ProjectFinanceNotesRestServiceImpl() {
        super("/project/finance/notes", new ParameterizedTypeReference<List<NoteResource>>() {
        });
    }

}