package org.innovateuk.ifs.alert.controller;

import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.innovateuk.ifs.alert.transactional.AlertService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes CRUD operations through a REST API to manage {@link org.innovateuk.ifs.alert.domain.Alert} related data.
 */
@RestController
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping(value = "/findAllVisible")
    public RestResult<List<AlertResource>> findAllVisible() {
        return alertService.findAllVisible().toGetResponse();
    }

    @GetMapping(value = "/findAllVisible/{type}")
    public RestResult<List<AlertResource>> findAllVisibleByType(@PathVariable("type") AlertType type) {
        return alertService.findAllVisibleByType(type).toGetResponse();
    }

    @GetMapping(value = "/{id}")
    public RestResult<AlertResource> findById(@PathVariable("id") Long id) {
        return alertService.findById(id).toGetResponse();
    }

    @PostMapping(value = "/")
    public RestResult<AlertResource> create(@RequestBody AlertResource alertResource) {
        return alertService.create(alertResource).toPostCreateResponse();
    }

    @DeleteMapping(value = "/{id}")
    public RestResult<Void> delete(@PathVariable("id") Long id) {
        return alertService.delete(id).toDeleteResponse();
    }

    @DeleteMapping(value = "/delete/{type}")
    public RestResult<Void> deleteAllByType(@PathVariable("type") AlertType type) {
        return alertService.deleteAllByType(type).toDeleteResponse();
    }
}
