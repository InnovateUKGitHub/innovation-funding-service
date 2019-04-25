package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.transactional.RejectionReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link RejectionReason} related data.
 */
@RestController
@ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
@RequestMapping({"/rejectionReason", "/rejection-reason"})
public class RejectionReasonController {

    @Autowired
    private RejectionReasonService rejectionReasonService;

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findAllActive", "/find-all-active"})
    public RestResult<List<RejectionReasonResource>> findAllActive() {
        return rejectionReasonService.findAllActive().toGetResponse();
    }
}
