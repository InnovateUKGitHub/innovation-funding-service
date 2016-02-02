package com.worth.ifs.commons.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceFailure;

import java.util.Optional;

/**
 *
 */
public interface ServiceFailureToRestResultConverter {

    Optional<RestResult<?>> handle(ServiceFailure serviceFailure);

}
