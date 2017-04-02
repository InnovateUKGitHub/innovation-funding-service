package org.innovateuk.ifs.alert;

import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.innovateuk.ifs.application.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/alert")
@PreAuthorize("permitAll")
public class  AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping("/findAllVisibleByType/{type}")
    public @ResponseBody
    List<AlertResource> getAlertByTypeJSON(@PathVariable("type") AlertType type) {
        return alertService.findAllVisibleByType(type);
    }
}
