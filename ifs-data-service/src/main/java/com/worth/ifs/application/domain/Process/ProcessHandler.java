package com.worth.ifs.application.domain.Process;

import com.worth.ifs.application.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by nunoalexandre on 15/09/15.
 */

@Component("ProcessHandler")
public class ProcessHandler implements IProcessHandler {

    @Autowired
    private ProcessRepository repository;

    @Override
    public IProcess getProcessById(Long id) {
        return repository.findById(id);
    }

    @Override
    public  List<IProcess> getProcessesByInvolved(Long involved) {
        return repository.findByInvolvedId(involved);
    }

    @Override
    public  List<IProcess> getProcessesByTarget(Long target) {
        return repository.findByTargetId(target);
    }

    @Override
    public  List<IProcess> getProcessesByInvolvedAndType(Long involved, ProcessType type) {
        return repository.findByInvolvedIdAndType(involved, type);
    }

    @Override
    public  List<IProcess> getProcessesByTargetAndType(Long target, ProcessType type) {
        return repository.findByTargetIdAndType(target, type);
    }

    @Override
    public  List<IProcess> getProcessesByTargetAndTypeAndStatus(Long target, ProcessType type, ProcessStatus status) {
        return repository.findByInvolvedIdAndTypeAndStatus(target, type, status);
    }

    @Override
    public List<Process> getAllProcesses(){
        return repository.findAll();
    }
}
