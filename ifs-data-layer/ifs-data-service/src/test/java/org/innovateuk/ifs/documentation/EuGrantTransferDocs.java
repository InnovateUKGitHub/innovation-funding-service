package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResourceBuilder.newEuGrantTransferResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class EuGrantTransferDocs {
    public static final FieldDescriptor[] euGrantTransferResourceFields = {
            fieldWithPath("grantAgreementNumber").description("This might also be referred to as your project ID."),
            fieldWithPath("participantId").description("The 9-digit number unique to your organisation."),
            fieldWithPath("projectName").description("The name of your project."),
            fieldWithPath("projectStartDate").description("The date the project will start."),
            fieldWithPath("projectEndDate").description("The date the project will end."),
            fieldWithPath("fundingContribution").description("The total amount in euros granted to your organisation."),
            fieldWithPath("projectCoordinator").description("Is your organisation the consortium lead on this project."),
            fieldWithPath("actionType").description("The funding scheme you applied for."),
            fieldWithPath("actionType.id").description("The id of the funding scheme you applied for."),
            fieldWithPath("actionType.name").description("The name of the funding scheme you applied for."),
            fieldWithPath("actionType.description").description("The description of the funding scheme you applied for."),
            fieldWithPath("actionType.priority").description("The priority of the funding scheme you applied for."),
    };

    public static final EuGrantTransferResource EU_GRANT_TRANSFER_RESOURCE;

    static {
        EuActionTypeResource euActionTypeResource = new EuActionTypeResource();
        euActionTypeResource.setId(1L);

        EU_GRANT_TRANSFER_RESOURCE = newEuGrantTransferResource()
                .withActionType(euActionTypeResource)
                .withFundingContribution(BigDecimal.valueOf(100000L))
                .withGrantAgreementNumber("123456")
                .withProjectCoordinator(true)
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now().plusYears(1L))
                .withProjectName("Project Name")
                .withParticipantId("123456789")
                .build();
    }
}