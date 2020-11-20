package org.innovateuk.ifs.virtualassistant;

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.ui.Model;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.innovateuk.ifs.virtualassistant.VirtualAssistantController.MODEL;

public class VirtualAssistantTestHelper {

    public static ResultMatcher assertVirtualAssistantModel(String botId,
                                        String authToken, String errorMessage, boolean isServerAvailable) {
        return result -> {
            assertVirtualAssistantModel(result.getModelAndView().getModelMap(), botId, authToken, errorMessage, isServerAvailable);
        };
    }

    public static void assertVirtualAssistantModel(Model model, String botId,
                                                   String authToken, String errorMessage, boolean isServerAvailable){
        assertVirtualAssistantModel(model.asMap(), botId, authToken, errorMessage, isServerAvailable);
    }

    public static void assertVirtualAssistantModel(Map<String, Object> modelMap, String botId,
                                                   String authToken, String errorMessage, boolean isServerAvailable){
        VirtualAssistantModel virtualAssistantModel = (VirtualAssistantModel) modelMap.get(MODEL);
        assertThat(virtualAssistantModel.getBotId(), equalTo(botId));
        assertThat(virtualAssistantModel.getClientToken(), equalTo(authToken));
        assertThat(virtualAssistantModel.getErrorMessage(), equalTo(errorMessage));
        assertThat(virtualAssistantModel.isServerAvailable(), equalTo(isServerAvailable));
    }

}
