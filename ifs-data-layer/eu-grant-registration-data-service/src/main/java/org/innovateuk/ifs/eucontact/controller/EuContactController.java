package org.innovateuk.ifs.eucontact.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eucontact.transactional.EuContactService;
import org.innovateuk.ifs.eugrant.EuContactPageResource;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for saving and getting details of eu contacts.
 */
@RestController
public class EuContactController {

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "100";
    private static final Sort sort = new Sort("id");

    @Autowired
    EuContactService euContactService;

    @GetMapping("/eu-contacts/notified/{notified}")
    public RestResult<EuContactPageResource> getEuContactsByNotified(@PathVariable("notified") boolean notified,
                                                                     @RequestParam(value = "page",defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                     @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {

        return euContactService.getEuContactsByNotified(notified, new PageRequest(pageIndex, pageSize, sort)).toGetResponse();
    }
}