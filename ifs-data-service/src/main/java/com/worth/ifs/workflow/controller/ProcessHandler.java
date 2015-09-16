package com.worth.ifs.workflow.controller;

import com.worth.ifs.application.repository.ProcessRepository;
import com.worth.ifs.workflow.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.*;
import java.lang.Process;
import java.util.List;

/**
 * Created by nunoalexandre on 15/09/15.
 */

@Component
public class ProcessHandler {

    @Autowired
    private ProcessRepository repository;

    public void saveProcess(com.worth.ifs.workflow.domain.Process process) {
        repository.save(process);
    }
    public com.worth.ifs.workflow.domain.Process saveProcessAndGetUpdated(com.worth.ifs.workflow.domain.Process process) {
        return repository.save(process);
    }


    public com.worth.ifs.workflow.domain.Process getProcessById(Long id) {
        return repository.findById(id);
    }

    public  List<com.worth.ifs.workflow.domain.Process> getProcessesByAssignee(Long involved) {
        return repository.findByAssigneeId(involved);
    }

    public  List<com.worth.ifs.workflow.domain.Process> getProcessesBySubject(Long subject) {
        return repository.findBySubjectId(subject);
    }

    public  List<com.worth.ifs.workflow.domain.Process> getProcessesByAssigneeAndType(Long assignee, ProcessEvent event) {
        return repository.findByAssigneeIdAndEvent(assignee, event);
    }

    public  List<com.worth.ifs.workflow.domain.Process> getProcessesBySubjectAndType(Long subject, ProcessEvent event) {
        return repository.findBySubjectIdAndEvent(subject, event);
    }

    public  List<com.worth.ifs.workflow.domain.Process> getProcessesBySubjectAndTypeAndStatus(Long subject, ProcessEvent event, ProcessStatus status) {
        return repository.findByAssigneeIdAndEventAndStatus(subject, event, status);
    }

    public List<com.worth.ifs.workflow.domain.Process> getAllProcesses(){
        return repository.findAll();
    }
}
