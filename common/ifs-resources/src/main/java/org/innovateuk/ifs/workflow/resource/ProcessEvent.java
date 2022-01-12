package org.innovateuk.ifs.workflow.resource;

/**
 * The process events should be represented by a named event.
 * These are used to progress through the workflow.
 */
public interface ProcessEvent {
    String getType();
}
