package com.worth.ifs.workflow.controller;

import com.worth.ifs.application.repository.AssessmentProcessRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.domain.AssessmentProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by nunoalexandre on 15/09/15.
 */

@Component
public class AssessmentProcessHandler {

    @Autowired
    private AssessmentProcessRepository repository;

    public void saveProcess(AssessmentProcess process) {
        repository.save(process);
    }
    public AssessmentProcess saveProcessAndGetUpdated(AssessmentProcess process) {
        return repository.save(process);
    }

    public List<AssessmentProcess> getByAssessorAndCompetition(User assessor, Competition competition) {
        return repository.findByAssessorAndApplicationCompetition(assessor,competition);
    }



//    public com.worth.ifs.workflow.domain.Process getProcessById(Long id) {
//        return repository.findOne(id);
//    }
//
//    public  List<com.worth.ifs.workflow.domain.Process> getProcessesByAssignee(Long involved) {
//        return repository.findByAssigneeId(involved);
//    }
//
//    public  List<com.worth.ifs.workflow.domain.Process> getProcessesBySubject(Long subject) {
//        return repository.findBySubjectId(subject);
//    }
//
//    public  List<com.worth.ifs.workflow.domain.Process> getProcessesByAssigneeAndEvent(Long assignee, ProcessEvent event) {
//        return repository.findByAssigneeIdAndEvent(assignee, event);
//    }
//
//    public  List<com.worth.ifs.workflow.domain.Process> getProcessesBySubjectAndEvent(Long subject, ProcessEvent event) {
//        return repository.findBySubjectIdAndEvent(subject, event);
//    }
//
//    public  List<com.worth.ifs.workflow.domain.Process> getProcessesBySubjectAndEventAndStatus(Long subject, ProcessEvent event, ProcessStatus status) {
//        return repository.findByAssigneeIdAndEventAndStatus(subject, event, status);
//    }

//    public List<com.worth.ifs.workflow.domain.Process> getAllProcesses(){
//        return repository.findAll();
//    }
}
