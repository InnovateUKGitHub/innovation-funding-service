package com.worth.ifs.commons.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Given a request, this component will be able to return an authentication token using an appropriate strategy
 */
public interface UidSupplier {

    String getUid(HttpServletRequest request);
}
