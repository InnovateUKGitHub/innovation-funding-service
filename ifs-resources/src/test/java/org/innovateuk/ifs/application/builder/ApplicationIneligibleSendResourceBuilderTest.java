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
        String content = "content";

        ApplicationIneligibleSendResource resource = newApplicationIneligibleSendResource()
                .withSubject(subject)
                .withContent(content)
                .build();

        assertEquals(subject, resource.getSubject());
        assertEquals(content, resource.getContent());
    }

    @Test
    public void buildMany() {
        String[] subjects = {"subject", "otherSubject"};
        String[] contents = {"content", "otherContent"};

        List<ApplicationIneligibleSendResource> resources = newApplicationIneligibleSendResource()
                .withSubject(subjects)
                .withContent(contents)
                .build(2);

        for (int i = 0; i < resources.size(); i++) {
            assertEquals(subjects[i], resources.get(i).getSubject());
            assertEquals(contents[i], resources.get(i).getContent());
        }
    }
}
