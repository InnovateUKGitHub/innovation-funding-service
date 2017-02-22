package org.innovateuk.ifs.threads.attachments.controller;

import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceQueriesAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project/finance/attachment")
public class ProjectFinancePostAttachmentsController extends AttachmentController {

    @Autowired
    public ProjectFinancePostAttachmentsController(ProjectFinanceQueriesAttachmentService service) {
        super(service);
    }
}