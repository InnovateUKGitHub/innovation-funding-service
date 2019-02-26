package org.innovateuk.ifs.eucontact.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eucontact.transactional.EuContactService;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.euinvite.EuInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for saving and getting details of eu contacts.
 */

@RestController
@RequestMapping("/eu-contacts")
public class EuContactController {


    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "100";
    private static final Sort sort = new Sort("id");

    @Autowired
    private EuContactService euContactService;

    @Autowired
    private EuInviteService euInviteService;

    @GetMapping("/notified/{notified}")
    public RestResult<EuContactPageResource> getEuContactsByNotified(@PathVariable("notified") boolean notified,
                                                                     @RequestParam(value = "page",defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                     @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {

        return euContactService.getEuContactsByNotified(notified, new PageRequest(pageIndex, pageSize, sort)).toGetResponse();
    }

    @PostMapping("/send-invites")
    public RestResult<Void> sendInvites(@RequestBody List<Long> ids) {
        return euInviteService.sendInvites(ids).toPostResponse();
    }
}