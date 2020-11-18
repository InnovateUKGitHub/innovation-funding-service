package org.innovateuk.ifs.virtualassistant;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/virtual-assistant")
@SecuredBySpring(value = "Controller", description = "Set up virtual assistant auth", securedType = VirtualAssistantController.class)
@PreAuthorize("permitAll")
public class VirtualAssistantController {

    @Autowired
    public VirtualAssistantAuthRestClient virtualAssistantAuthRestClient;

    @GetMapping
    public String virtualAssistant(Model model) {
        VirtualAssistantModel virtualAssistantModel = virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails();
        model.addAttribute("virtualAssistantBotId", virtualAssistantModel.getBotId());
        model.addAttribute("virtualAssistantClientToken", virtualAssistantModel.getClientToken());
        model.addAttribute("virtualAssistantErrorMessage", virtualAssistantModel.getErrorMessage());
        model.addAttribute("virtualAssistantServerAvailable", virtualAssistantModel.isServerAvailable());
        return "virtual-assistant";
    }

}
