package com.worth.ifs.user.transactional;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.OrganisationType;

public interface OrganisationTypeService {
    @NotSecured("Public objects, just a collection of all different organisation types.")
    OrganisationType findOne(Long id);
    @NotSecured("Public objects, just a collection of all different organisation types.")
    Iterable<OrganisationType> findAll();
}