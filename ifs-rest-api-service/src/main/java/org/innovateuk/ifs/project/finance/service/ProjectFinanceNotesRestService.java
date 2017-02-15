package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.thread.service.ThreadRestService;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectFinanceNotesRestService extends ThreadRestService<NoteResource> {

    public ProjectFinanceNotesRestService() {
        super("/project/finance/notes", NoteResource.class, new ParameterizedTypeReference<List<NoteResource>>() {
        });
    }

}