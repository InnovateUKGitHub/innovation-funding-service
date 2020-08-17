package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.transactional.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organisation/knowledge-base")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @GetMapping
    public RestResult<List<String>> getKnowledgeBases() {
        return knowledgeBaseService.getKnowledegeBases().toGetResponse();
    }

    @GetMapping("/{id}")
    public RestResult<String> getKnowledgeBase(@PathVariable long id) {
        return knowledgeBaseService.getKnowledegeBase(id).toGetResponse();
    }
}
