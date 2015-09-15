package com.worth.ifs.application.domain.Process;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by nunoalexandre on 15/09/15.
 */

public enum ProcessStatus {

    /***  All types of status  ***/
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    PENDING("pending");

    //the status string value
    private final String status;

    //creates the enum with the choosen type.
    ProcessStatus(String value) {
        status = value;
    }
}
