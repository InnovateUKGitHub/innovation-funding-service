package com.worth.ifs.workflow.domain;

import com.worth.ifs.application.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.*;
import java.util.List;

/**
 * Created by nunoalexandre on 15/09/15.
 */

public class ProcessHandler {

    @Autowired
    private ProcessRepository repository;



    public Process getProcessById(Long id) {
        return repository.findById(id);
    }

    public  List<Process> getProcessesByAssignee(Long involved) {
        return repository.findByAssigneeId(involved);
    }

    public  List<Process> getProcessesBySubject(Long subject) {
        return repository.findBySubjectId(subject);
    }

    public  List<Process> getProcessesByAssigneeAndType(Long assignee, ProcessEvent event) {
        return repository.findByAssigneeIdAndEvent(assignee, event);
    }

    public  List<Process> getProcessesBySubjectAndType(Long subject, ProcessEvent event) {
        return repository.findBySubjectIdAndEvent(subject, event);
    }

    public  List<Process> getProcessesBySubjectAndTypeAndStatus(Long subject, ProcessEvent event, ProcessStatus status) {
        return repository.findByAssigneeIdAndEventAndStatus(subject, event, status);
    }

    public List<Process> getAllProcesses(){
        return repository.findAll();
    }
}
