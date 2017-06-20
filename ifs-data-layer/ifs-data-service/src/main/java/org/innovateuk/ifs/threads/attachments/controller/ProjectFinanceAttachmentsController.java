package org.innovateuk.ifs.threads.attachments.controller;

import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project/finance/attachments")
public class ProjectFinanceAttachmentsController extends AttachmentController {

    @Autowired
    public ProjectFinanceAttachmentsController(ProjectFinanceAttachmentService service) {
        super(service);
    }
}