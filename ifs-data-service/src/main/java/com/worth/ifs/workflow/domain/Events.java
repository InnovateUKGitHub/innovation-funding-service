package com.worth.ifs.workflow.domain;

/**
 * The process events should be represented by a named event.
 * These are used to progress through the workflow.
 */
public interface Events {
    public String getEvent();
}
