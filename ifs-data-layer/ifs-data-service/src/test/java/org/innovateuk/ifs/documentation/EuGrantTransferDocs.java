package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResourceBuilder.newEuGrantTransferResource;

public class EuGrantTransferDocs {

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