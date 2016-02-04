package com.worth.ifs.user.transactional;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.OrganisationType;

public interface OrganisationTypeService {
    @NotSecured("TODO")
    OrganisationType findOne(Long id);
}