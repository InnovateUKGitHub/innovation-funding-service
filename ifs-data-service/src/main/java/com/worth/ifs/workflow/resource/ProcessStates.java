package com.worth.ifs.workflow.resource;

/**
 * The process states should be represented by a named state.
 * These are used to progress through the workflow.
 */
public interface ProcessStates {

    String getStateName();

    State getBackingState();
}
