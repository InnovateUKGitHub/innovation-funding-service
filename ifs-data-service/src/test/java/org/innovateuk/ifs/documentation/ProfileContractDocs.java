package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.ProfileContractResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDateTime;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.innovateuk.ifs.user.builder.ProfileContractResourceBuilder.newProfileContractResource;
import static java.lang.Boolean.TRUE;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ProfileContractDocs {
    public static final FieldDescriptor[] profileContractResourceFields = {
            fieldWithPath("user").description("Assessor user associated with the profile contract"),
            fieldWithPath("contract").description("The current contract"),
            fieldWithPath("currentAgreement").description("Flag to signify if the user has a current agreement with the contract"),
            fieldWithPath("contractSignedDate").description("Date and time that a contract was agreed to)"),
    };

    public static final ProfileContractResourceBuilder profileContractResourceBuilder = newProfileContractResource()
            .withUser(1L)
            .withContract(newContractResource()
                    .with(id(1L))
                    .withCurrent(TRUE)
                    .withText("Contract text...")
                    .withAnnexA("Annex A text...")
                    .withAnnexB("Annex B text...")
                    .withAnnexC("Annex C text...")
                    .build())
            .withCurrentAgreement(TRUE)
            .withContractSignedDate(LocalDateTime.now());
}
