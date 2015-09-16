package com.worth.ifs.assessment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * AssessmentRestRestServiceIpml is a utility to use client-side to retrieve Assessment data from the data-service controllers.
 */

@Service
public class AssessmentRestImpl {

    @Value("${ifs.data.service.rest.assessment}")
    String assessmentRestURL;





}
