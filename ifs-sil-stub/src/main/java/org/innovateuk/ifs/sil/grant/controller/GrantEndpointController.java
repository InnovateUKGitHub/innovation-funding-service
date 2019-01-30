package org.innovateuk.ifs.sil.grant.controller;

import com.google.common.collect.EvictingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Mock grant endpoint for sending project to grant monitoring system.   Note that the last 100 events are stored in
 * a memory buffer can be retrieved via the HTTP endpoints.
 *
 * <code>
 * curl localhost:8080/silstub/sendproject/events
 * curl localhost:8080/silstub/sendproject/event/108
 * </code>
 */
@RestController
@RequestMapping("/silstub")
public class GrantEndpointController {
    private static final Log LOG = LogFactory.getLog(GrantEndpointController.class);
    private static final EvictingQueue<Event> history = EvictingQueue.create(100);

    @PostMapping("/sendproject")
    public RestResult<Void> sendProject(@RequestBody List<Grant> grantAsList) {

        Grant grant = grantAsList.get(0);

        LOG.info("Grant data send to stub : JSON = " + JsonMappingUtil.toJson(grant));
        history.add(new Event(grant));

        LOG.info("Grant data sent to stub : Summary = " + getSummary(grant));
        return restSuccess(HttpStatus.ACCEPTED);
    }

    @GetMapping("/sendproject/events")
    public RestResult<List<Event>> getAllEvents() {
        return serviceSuccess((List<Event>) new ArrayList<>(history))
                .toGetResponse();
    }

    @GetMapping("/sendproject/event/{applicationId}")
    public RestResult<Event> getEvent(@PathVariable("applicationId") long applicationId) {
        return serviceSuccess(history.stream()
                    .filter(event -> applicationId == event.getGrant().getId())
                    .findFirst().orElseThrow(IllegalStateException::new))
                .toGetResponse();
    }

    private String getSummary(Grant grant) {
        return grant.getId() + " with " + grant.getParticipants().size() + " participants";
    }

    public static class Event {
        private final Grant grant;

        private Event(Grant grant) {
            this.grant = grant;
        }

        Grant getGrant() {
            return grant;
        }
    }
}
