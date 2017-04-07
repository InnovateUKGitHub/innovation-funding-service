package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.ZonedDateTime;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProfileAgreementDocs {
    public static final FieldDescriptor[] profileAgreementResourceFields = {
            fieldWithPath("user").description("Assessor user associated with the profile agreement"),
            fieldWithPath("agreement").description("The current agreement"),
            fieldWithPath("currentAgreement").description("Flag to signify if the user has a current agreement"),
            fieldWithPath("agreementSignedDate").description("Date and time that the agreement was signed)"),
    };

    public static final ProfileAgreementResourceBuilder profileAgreementResourceBuilder = newProfileAgreementResource()
            .withUser(1L)
            .withAgreement(newAgreementResource()
                    .with(id(1L))
                    .withCurrent(TRUE)
                    .withText("Agreement text...")
                    .build())
            .withCurrentAgreement(TRUE)
            .withAgreementSignedDate(ZonedDateTime.now());
}
