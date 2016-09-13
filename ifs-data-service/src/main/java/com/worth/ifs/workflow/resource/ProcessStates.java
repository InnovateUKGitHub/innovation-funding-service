package com.worth.ifs.workflow.resource;

/**
 * The process states should be represented by a named state.
 * These are used to progress through the workflow.
 */
public interface ProcessStates {

    String getStateName();

    State getBackingState();

    static <T extends ProcessStates> T fromState(T[] values, State state) {
        for (T availableState : values) {
            if (availableState.getBackingState().equals(state)) {
                return availableState;
            }
        }
        return null;
    }
}
