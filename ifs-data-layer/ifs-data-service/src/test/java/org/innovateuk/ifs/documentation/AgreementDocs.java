package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.AgreementResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AgreementDocs {

    public static final AgreementResourceBuilder agreementResourceBuilder = newAgreementResource()
            .with(id(1L))
            .withCurrent(TRUE)
            .withText("Agreement text...");
}
