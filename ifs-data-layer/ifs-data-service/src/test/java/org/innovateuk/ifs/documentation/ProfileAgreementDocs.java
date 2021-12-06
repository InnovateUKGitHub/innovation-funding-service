package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder;

import java.time.ZonedDateTime;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;

public class ProfileAgreementDocs {

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
