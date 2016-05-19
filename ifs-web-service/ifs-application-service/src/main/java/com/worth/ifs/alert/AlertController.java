package com.worth.ifs.alert;

import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.alert.resource.AlertType;
import com.worth.ifs.application.service.AlertService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    AlertService alertService;

    private static Log LOG = LogFactory.getLog(AlertController.class);

    @RequestMapping(value = "/findAllVisibleByType/{type}", method = RequestMethod.GET)
    public @ResponseBody
    List<AlertResource> getAlertByTypeJSON(@PathVariable("type") String type) {
        List<AlertResource> alerts = new ArrayList();

        try {
            AlertType alertType = AlertType.valueOf(type.toUpperCase());
            alerts = alertService.findAllVisibleByType(alertType);
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getMessage());
        }

        return alerts;
    }
}
