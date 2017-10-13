package org.innovateuk.ifs.commons.security;

import org.springframework.stereotype.Service;


// TODO qqRP This project is dependent on ifs-commons which has a UidAuthenticationService service.
// TODO qqRP This class shadows it so we don't need to worry about its dependencies. This is a short term solution.
// TODO qqRP Long term solution is to make the UidAuthenticationService not a service in ifs-common.
// TODO qqRP Projects Where it needs to be service would declare it.
@Service
public class UidAuthenticationService {

}
