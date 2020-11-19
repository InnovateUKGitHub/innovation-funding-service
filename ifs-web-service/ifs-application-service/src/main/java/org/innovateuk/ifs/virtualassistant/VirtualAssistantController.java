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
    protected static final String REQUEST_MAPPING = "/virtual-assistant";
    protected static final String VIRTUAL_ASSISTANT_BOT_ID = "virtualAssistantBotId";
    protected static final String VIRTUAL_ASSISTANT_CLIENT_TOKEN = "virtualAssistantClientToken";
    protected static final String VIRTUAL_ASSISTANT_ERROR_MESSAGE = "virtualAssistantErrorMessage";
    protected static final String VIRTUAL_ASSISTANT_SERVER_AVAILABLE = "virtualAssistantServerAvailable";

    @Autowired
    private VirtualAssistantAuthRestClient virtualAssistantAuthRestClient;

    @GetMapping
    public String virtualAssistant(Model model) {
        VirtualAssistantModel virtualAssistantModel = virtualAssistantAuthRestClient.obtainVirtualAssistantAuthDetails();
        model.addAttribute(VIRTUAL_ASSISTANT_BOT_ID, virtualAssistantModel.getBotId());
        model.addAttribute(VIRTUAL_ASSISTANT_CLIENT_TOKEN, virtualAssistantModel.getClientToken());
        model.addAttribute(VIRTUAL_ASSISTANT_ERROR_MESSAGE, virtualAssistantModel.getErrorMessage());
        model.addAttribute(VIRTUAL_ASSISTANT_SERVER_AVAILABLE, virtualAssistantModel.isServerAvailable());
        return THYMELEAF_MAPPING;
    }

}
