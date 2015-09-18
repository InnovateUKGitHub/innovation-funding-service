package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ApplicationController exposes Application data through a REST API.
 */
@RestController
@RequestMapping("/application")
public class ApplicationController {
    @Autowired
    ApplicationRepository repository;
    @Autowired
    ProcessRoleRepository userAppRoleRepository;
    @Autowired
    ApplicationStatusRepository applicationStatusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResponseRepository responseRepository;

    QuestionController questionController = new QuestionController();

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/id/{id}")
    public Application getApplicationById(@PathVariable("id") final Long id) {
        return repository.findOne(id);
    }

    @RequestMapping("/findAll")
     public List<Application> findAll() {
        List<Application> applications = repository.findAll();
        return applications;
    }

    @RequestMapping("/findByUser/{userId}")
    public List<Application> findByUserId(@PathVariable("userId") final Long userId) {
        User user = userRepository.findOne(userId);
        List<ProcessRole> roles =  userAppRoleRepository.findByUser(user);
        List<Application> apps = new ArrayList<>();
        for (ProcessRole role : roles) {
            apps.add(role.getApplication());
        }
        return apps;
    }

    /**
     * This method saves only a few application attributes that
     * the user is able to modify on the application form.
     */
    @RequestMapping("/saveApplicationDetails/{id}")
    public ResponseEntity<String> saveApplicationDetails(@PathVariable("id") final Long id,
                                                         @RequestBody Application application) {

        Application applicationDb = repository.findOne(id);
        HttpStatus status;

        if (applicationDb != null) {
            applicationDb.setName(application.getName());
            applicationDb.setDurationInMonths(application.getDurationInMonths());
            applicationDb.setStartDate(application.getStartDate());
            repository.save(applicationDb);

            status = HttpStatus.OK;

        }else{
            log.error("NOT_FOUND "+ id);
            status = HttpStatus.NOT_FOUND;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(headers, status);
    }

    @RequestMapping("/getProgressPercentageByApplicationId/{applicationId}")
    public ObjectNode getProgressPercentageByApplicationId(@PathVariable("applicationId") final Long applicationId) {
        Application application = repository.findOne(applicationId);
        List<Section> sections = application.getCompetition().getSections();
        List<Question> questions = sections.stream()
                .flatMap((s) -> s.getQuestions().stream())
                .filter((q -> q.isMarkAsCompletedEnabled()))
                .collect(Collectors.toList());

        List<ProcessRole> processRoles = application.getProcessRoles();
        Set<Organisation> organisations = processRoles.stream().map(p -> p.getOrganisation()).collect(Collectors.toSet());

        Long countMultipleStatusQuestionsCompleted = organisations.stream()
                .mapToLong(org -> questions.stream()
                        .filter(q -> q.hasMultipleStatuses() && q.isMarkedAsComplete(org.getId())).count())
                .sum();
        Long countSingleStatusQuestionsCompleted = questions.stream()
                        .filter(q -> !q.hasMultipleStatuses() && q.isMarkedAsComplete(0L)).count();
        Long countCompleted = countMultipleStatusQuestionsCompleted + countSingleStatusQuestionsCompleted;

        Long totalMultipleStatusQuestions = questions.stream().filter(q -> q.hasMultipleStatuses()).count() * organisations.size();
        Long totalSingleStatusQuestions = questions.stream().filter(q -> !q.hasMultipleStatuses()).count();

        Long totalQuestions = totalMultipleStatusQuestions + totalSingleStatusQuestions;
        log.debug("Total questions" + totalQuestions);
        log.debug("Total completed questions" + countCompleted);

        double percentageCompleted;
        if(questions.size() == 0){
            percentageCompleted = 0;
        }else{
            percentageCompleted = (100 / totalQuestions) * countCompleted;
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("completedPercentage", percentageCompleted);
        return node;
    }

    @RequestMapping(value="/updateApplicationStatus", method=RequestMethod.GET)
    public ResponseEntity<String> updateApplicationStatus(@RequestParam("applicationId") final Long id,
                                                          @RequestParam("statusId") final Long statusId){

        Application application = repository.findOne(id);
        ApplicationStatus applicationStatus = applicationStatusRepository.findOne(statusId);
        application.setApplicationStatus(applicationStatus);
        repository.save(application);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(headers, status);

    }


    @RequestMapping("/getApplicationsByCompetitionIdAndUserId/{competitionId}/{userId}/{role}")
    public List<Application> getApplicationsByCompetitionIdAndUserId(@PathVariable("competitionId") final Long competitionId,
                                                                     @PathVariable("userId") final Long userId,
                                                                     @PathVariable("role") final UserRoleType role) {
        User user = userRepository.findOne(userId);

        List<ProcessRole> roles =  userAppRoleRepository.findByUser(user);
        List<Application> allApps= repository.findAll();
        List<Application> apps = new ArrayList<>();
        for (Application app : allApps) {
            if ( app.getCompetition().getId().equals(competitionId) && applicationContainsUserRole(app.getProcessRoles(), userId, role)  ) {
                apps.add(app);
            }
        }
        return apps;
    }

    private boolean applicationContainsUserRole(List<ProcessRole> roles, final Long userId, UserRoleType role) {
        boolean contains = false;
        int i = 0;
        while( !contains && i < roles.size()) {
            contains = roles.get(i).getUser().getId().equals(userId) && roles.get(i).getRole().getName().equals(role.getName());
            i++;
        }

        return contains;
    }


}
