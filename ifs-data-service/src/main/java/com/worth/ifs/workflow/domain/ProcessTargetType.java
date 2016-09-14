package com.worth.ifs.workflow.domain;

/**
 * Holds a list of target types for Processes that the IFS application knows about.  Targets of Processes tend to
 * indicate the "thing" which is going through the process or that owns the "thing" that is going through the process.
 */
public enum ProcessTargetType {

    APPLICATION,
    PROJECT,
    PROJECT_PARTICIPANT_ORGANISATION
}
