package com.worth.ifs.invite.transactional;

import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.security.NotSecured;

public interface InviteOrganisationService {
    @NotSecured("TODO")
    InviteOrganisation findOne(Long id);
}