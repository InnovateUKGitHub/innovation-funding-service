package org.innovateuk.ifs.workflow.resource;

/**
 * The process states should identify valid states for their given Process to exist in.
 * These are used to progress through the workflow.
 * Each valid state in this enum implementation is backed by the persistent entity State that will
 * actually be stored in the database.
 */
public interface ProcessState {

    String getStateName();

    /**
     * The "backing state" represents the entity model State that is actually stored in the database.
     * Each ProcessState is mapped to a "real" State that is part of the persistent model.
     */
    State getBackingState();

    static <T extends ProcessState> T fromState(T[] values, State state) {
        for (T availableState : values) {
            if (availableState.getBackingState().equals(state)) {
                return availableState;
            }
        }
        return null;
    }
}
