package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.junit.Assert.assertEquals;

public class ApplicationIneligibleSendResourceBuilderTest {

    @Test
    public void buildOne() {
        String subject = "subject";
        String message = "message";

        ApplicationIneligibleSendResource resource = newApplicationIneligibleSendResource()
                .withSubject(subject)
                .withMessage(message)
                .build();

        assertEquals(subject, resource.getSubject());
        assertEquals(message, resource.getMessage());
    }

    @Test
    public void buildMany() {
        String[] subjects = {"subject", "otherSubject"};
        String[] messages = {"message", "otherMessage"};

        List<ApplicationIneligibleSendResource> resources = newApplicationIneligibleSendResource()
                .withSubject(subjects)
                .withMessage(messages)
                .build(2);

        for (int i = 0; i < resources.size(); i++) {
            assertEquals(subjects[i], resources.get(i).getSubject());
            assertEquals(messages[i], resources.get(i).getMessage());
        }
    }
}
