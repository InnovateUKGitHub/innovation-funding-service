package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApplicationIneligibleSendResourceDocs {

    public static final ApplicationIneligibleSendResourceBuilder applicationIneligibleSendResourceBuilder =
            newApplicationIneligibleSendResource()
                    .withSubject("Subject line")
                    .withMessage("Message content");
}
