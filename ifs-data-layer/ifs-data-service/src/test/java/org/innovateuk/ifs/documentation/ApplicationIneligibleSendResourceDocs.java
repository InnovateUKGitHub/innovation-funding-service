package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder;

import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;

public class ApplicationIneligibleSendResourceDocs {

    public static final ApplicationIneligibleSendResourceBuilder applicationIneligibleSendResourceBuilder =
            newApplicationIneligibleSendResource()
                    .withSubject("Subject line")
                    .withMessage("Message content");
}
