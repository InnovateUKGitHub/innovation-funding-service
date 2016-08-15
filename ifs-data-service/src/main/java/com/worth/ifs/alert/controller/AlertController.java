package com.worth.ifs.alert.controller;

import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.alert.transactional.AlertService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Exposes CRUD operations through a REST API to manage {@link com.worth.ifs.alert.domain.Alert} related data.
 */
@RestController
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @RequestMapping(value = "/findAllVisible", method = GET)
    public RestResult<List<AlertResource>> findAllVisible() {
        return alertService.findAllVisible().toGetResponse();
    }

    @RequestMapping(value = "/findAllVisible/{type}", method = GET)
    public RestResult<List<AlertResource>> findAllVisibleByType(@PathVariable("type") AlertType type) {
        return alertService.findAllVisibleByType(type).toGetResponse();
    }

    @RequestMapping(value = "/{id}", method = GET)
    public RestResult<AlertResource> findById(@PathVariable("id") Long id) {
        return alertService.findById(id).toGetResponse();
    }

    @RequestMapping(value = "/", method = POST)
    public RestResult<AlertResource> create(@RequestBody AlertResource alertResource) {
        return alertService.create(alertResource).toPostCreateResponse();
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public RestResult<Void> delete(@PathVariable("id") Long id) {
        return alertService.delete(id).toDeleteResponse();
    }

    @RequestMapping(value = "/delete/{type}", method = DELETE)
    public RestResult<Void> deleteAllByType(@PathVariable("type") AlertType type) {
        return alertService.deleteAllByType(type).toDeleteResponse();
    }
}
