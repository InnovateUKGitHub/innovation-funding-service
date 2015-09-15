package com.worth.ifs.application.domain.Process;

import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by nunoalexandre on 15/09/15.
 */
public interface IProcess {

    /** Getters **/
    Long getInvolved();
    Long getTarget();
    Long getId();
    ProcessStatus getStatus();
    ProcessType getType();
    public Calendar getVersion();

    /** Setters **/
    public void setInvolved(Long involved);
    public void setStatus(ProcessStatus status);
    public void setType(ProcessType type);
    public void setTarget(Long target);

}
