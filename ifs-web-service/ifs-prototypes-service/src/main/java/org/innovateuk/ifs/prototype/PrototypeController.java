package org.innovateuk.ifs.prototype;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A controller to serve embedded prototype pages from the actual application during development.
 */
@Controller
@RequestMapping("/prototypes")
public class PrototypeController {

    private static final Log LOG = LogFactory.getLog(PrototypeController.class);

    @GetMapping
    public String getPrototypeIndex() {
        LOG.debug("Serving up prototype index page");
        return "prototypes/index";
    }

    @GetMapping("/{templateName}")
    public String getPrototypePage(@PathVariable("templateName") String templateName) {
        LOG.debug("Serving up prototype template " + templateName);
        return "prototypes/" + templateName;
    }
}
