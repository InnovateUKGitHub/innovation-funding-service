package org.innovateuk.ifs.project.notes.controller;

import org.innovateuk.ifs.project.notes.service.FinanceCheckNotesService;
import org.innovateuk.ifs.threads.controller.CommonThreadController;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project/finance/notes")
public class ProjectFinanceNotesController extends CommonThreadController<NoteResource> {

    @Autowired
    public ProjectFinanceNotesController(FinanceCheckNotesService service) {
        super(service);
    }
}