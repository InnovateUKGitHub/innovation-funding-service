package org.innovateuk.ifs.virtualassistant;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(VirtualAssistantController.REQUEST_MAPPING)
@SecuredBySpring(value = "Controller", description = "Set up virtual assistant auth", securedType = VirtualAssistantController.class)
@PreAuthorize("permitAll")
public class VirtualAssistantController {

    protected static final String THYMELEAF_MAPPING = "virtual-assistant";
    protected static final String REQUEST_MAPPING = "/info/contact/virtual-assistant";
    protected static final String MODEL = "model";

    @Autowired
    private VirtualAssistantAuthRestClient virtualAssistantAuthRestClient;

    @GetMapping
    public String virtualAssistant(Model model) {
        model.addAttribute(MODEL, virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails());
        return THYMELEAF_MAPPING;
    }

}
