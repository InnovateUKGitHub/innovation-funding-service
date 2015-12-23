package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.util.JsonStatusResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * An interface reporesenting a class that can take a ServiceFailure and attempt to transform it into a JsonStatusResponse.
 * This is for transforming ServiceFailures returned from the Service layer into appropriate HTTP Responses from the
 * Controllers.
 */
public interface ServiceFailureToJsonResponseHandler {

    Optional<JsonStatusResponse> handle(ServiceFailure serviceFailure, HttpServletResponse response);
}
