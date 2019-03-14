package org.innovateuk.ifs.eugrant.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuGrantPageResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.transactional.EuGrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for saving and getting eu grant registrations.
 */
@RestController
public class EuGrantController {

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "100";

    @Autowired
    private EuGrantService euGrantService;

    @PostMapping("/eu-grant")
    public RestResult<EuGrantResource> create() {
        return euGrantService.create().toPostCreateResponse();
    }

    @PutMapping("/eu-grant/{uuid}")
    public RestResult<Void> update(@PathVariable("uuid") UUID uuid,
                                   @RequestBody EuGrantResource euGrant) {
        return euGrantService.update(uuid, euGrant).toPutResponse();
    }

    @GetMapping("/eu-grant/{uuid}")
    public RestResult<EuGrantResource> getEuGrant(@PathVariable("uuid") UUID uuid) {
        return euGrantService.findById(uuid).toGetResponse();
    }

    @PostMapping("/eu-grant/{uuid}/submit")
    public RestResult<EuGrantResource> submit(@PathVariable("uuid") UUID uuid) {
        return euGrantService.submit(uuid, true).toPostWithBodyResponse();
    }

    @GetMapping("/eu-grants/notified/{notified}")
    public RestResult<EuGrantPageResource> getEuGrantsByContactNotified(@PathVariable("notified") boolean notified,
                                                                        @RequestParam(value = "page",defaultValue = DEFAULT_PAGE_NUMBER) int pageIndex,
                                                                        @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {

        Sort sort = notified ? new Sort("organisation.name", "contact.name") : new Sort("contact.id");
        return euGrantService.getEuGrantsByContactNotified(notified, new PageRequest(pageIndex, pageSize, sort)).toGetResponse();
    }

    @GetMapping("/eu-grants/total-submitted")
    public RestResult<Long> getTotalSubmitted() {
        return euGrantService.getTotalSubmitted().toGetResponse();
    }
}
