package org.innovateuk.ifs.questionnaire.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApplicationOrganisationLinkResource.class, name = "ApplicationOrganisationLink"),
        @JsonSubTypes.Type(value = ProjectOrganisationLinkResource.class, name = "ProjectOrganisationLink")
})
public abstract class QuestionnaireLinkResource {

    private String responseId;

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }
}
