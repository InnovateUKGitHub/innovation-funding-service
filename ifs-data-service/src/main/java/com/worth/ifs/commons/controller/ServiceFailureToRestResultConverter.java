package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.RestResult;
import com.worth.ifs.transactional.ServiceFailure;

import java.util.Optional;

/**
 *
 */
public interface ServiceFailureToRestResultConverter {

    Optional<RestResult<?>> handle(ServiceFailure serviceFailure);

}
