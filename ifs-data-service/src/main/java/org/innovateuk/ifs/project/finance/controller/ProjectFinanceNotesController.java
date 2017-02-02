package org.innovateuk.ifs.project.finance.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.threads.domain.Post;
import org.innovateuk.ifs.threads.domain.Note;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.innovateuk.threads.resource.NoteResource;
import org.innovateuk.threads.resource.PostResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/project/finance/notes")
public class ProjectFinanceNotesController {
    @Autowired
    ThreadService<Note, ProjectFinance> service;

    @RequestMapping(value = "/{projectFinanceId}", method = GET)
    public RestResult<List<NoteResource>> notes(@PathVariable("projectFinanceId") final Long projectFinanceId) {
        return service.findAll(projectFinanceId).toGetResponse();
    }

    @RequestMapping(value = "", method = POST)
    public RestResult<Void> createNote(@RequestBody NoteResource note ) {
        return service.create(note).toPostCreateResponse();
    }

    @RequestMapping(value = "/{noteId}/post", method = POST)
    public RestResult<Void> addPost(@PathVariable("noteId") Long noteId, @RequestBody PostResource post ) {
        return service.addPost(post, noteId).toPostCreateResponse();
    }

}
