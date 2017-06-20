package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder;
import org.innovateuk.ifs.invite.domain.Invite;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InviteOrganisationDocs {

    public static final FieldDescriptor[] inviteOrganisationFields = {
            fieldWithPath("id").description("Id of the InviteOrganisation"),
            fieldWithPath("organisationName").description("Name of the organisation"),
            fieldWithPath("organisationNameConfirmed").description("Confirmed name of the organisation"),
            fieldWithPath("organisation").description("Id of the organisation"),
            fieldWithPath("inviteResources").description("List of application invites")
    };

    public static final InviteOrganisationResourceBuilder inviteOrganisationResourceBuilder = newInviteOrganisationResource()
            .withOrganisationName("Ludlow")
            .withOrganisationNameConfirmed("Ludlow")
            .withOrganisation(2L)
            .withInviteResources(newApplicationInviteResource()
                    .withEmail("jessica.doe@ludlow.co.uk", "ryan.dell@ludlow.co.uk")
                    .withHash(Invite.generateInviteHash(), Invite.generateInviteHash())
                    .withName("Jessica Doe", "Ryan Dell")
                    .withStatus(SENT)
                    .withApplication(1L)
                    .build(2));
}