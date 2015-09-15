package com.worth.ifs.application.domain.Process;

import java.util.List;

/**
 * Created by nunoalexandre on 15/09/15.
 */
public interface IProcessHandler {

    public IProcess getProcessById(Long id);

    public List<IProcess> getProcessesByInvolved(Long involved);

    public  List<IProcess> getProcessesByTarget(Long target);

    public  List<IProcess> getProcessesByInvolvedAndType(Long involved, ProcessType type);

    public  List<IProcess> getProcessesByTargetAndType(Long target, ProcessType type);

    public  List<IProcess> getProcessesByTargetAndTypeAndStatus(Long target, ProcessType type, ProcessStatus status);

    public List<Process> getAllProcesses();
}
