package org.innovateuk.ifs.virtualassistant;

public class VirtualAssistantModel {

    private final String botId;
    private final String clientToken;
    private final String errorMessage;
    private final boolean isServerAvailable;

    public VirtualAssistantModel(String botId, String clientToken) {
        this.botId = botId;
        this.clientToken = clientToken;
        this.errorMessage = "";
        this.isServerAvailable = true;
    }

    public VirtualAssistantModel(String errorMessage) {
        this.botId = "noRemoteServer";
        this.clientToken = "noRemoteServer";
        this.errorMessage = errorMessage;
        this.isServerAvailable = false;
    }

    public String getBotId() {
        return botId;
    }

    public String getClientToken() {
        return clientToken;
    }

    public boolean isServerAvailable() {
        return isServerAvailable;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
