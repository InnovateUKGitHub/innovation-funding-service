package com.worth.ifs.workflow.domain;

import java.util.Calendar;

/**
 * Created by nunoalexandre on 15/09/15.
 */
public interface IProcess {

    /** Getters **/
    Long getAssignee();
    Long getSubject();
    Long getId();
    ProcessStatus getStatus();
    ProcessEvent getEvent();
    public Calendar getVersion();

    /** Setters **/
    public void setAssignee(Long assigneeId);
    public void setStatus(ProcessStatus status);
    public void setEvent(ProcessEvent event);
    public void setSubject(Long subjectId);

}
