package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.UserApplicationRoleRepository;
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
    UserApplicationRoleRepository userAppRoleRepository;
    @Autowired
    ApplicationStatusRepository applicationStatusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResponseRepository responseRepository;

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/id/{id}")
    public Application getApplicationById(@PathVariable("id") final Long id) {
        return repository.findById(id);
    }

    @RequestMapping("/findAll")
     public List<Application> findAll() {
        List<Application> applications = repository.findAll();
        return applications;
    }

    @RequestMapping("/findByUser/{userId}")
    public List<Application> findByUserId(@PathVariable("userId") final Long userId) {
        User user = userRepository.findById(userId).get(0);
        List<UserApplicationRole> roles =  userAppRoleRepository.findByUser(user);
        List<Application> apps = new ArrayList<>();
        for (UserApplicationRole role : roles) {
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


        Application app = repository.findOne(applicationId);
        List<UserApplicationRole> userAppRoles = app.getUserApplicationRoles();

        List<Response> responses = new ArrayList<Response>();
        for (UserApplicationRole userAppRole : userAppRoles) {
            responses.addAll(responseRepository.findByUpdatedBy(userAppRole));
        }

        int countCompleted = 0;
        for (Response response : responses) {
            if(response.getQuestion().isMarkAsCompletedEnabled() && response.isMarkedAsComplete()){
                countCompleted++;
            }
        }

        log.error("Total questions" + questions.size());
        log.error("Total completed questions" + countCompleted);

        double percentageCompleted;
        if(questions.size() == 0){
            percentageCompleted = 0;
        }else{
            percentageCompleted = (100 / questions.size()) * countCompleted;
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
        User user = userRepository.findById(userId).get(0);

        List<UserApplicationRole> roles =  userAppRoleRepository.findByUser(user);
        List<Application> allApps= repository.findAll();
        List<Application> apps = new ArrayList<>();
        for (Application app : allApps) {
            if ( app.getCompetition().getId().equals(competitionId) && applicationContainsUserRole(app.getUserApplicationRoles(), userId, role)  ) {
                apps.add(app);
            }
        }
        return apps;
    }

    private boolean applicationContainsUserRole(List<UserApplicationRole> roles, final Long userId, UserRoleType role) {
        boolean contains = false;
        int i = 0;
        while( !contains && i < roles.size()) {
            contains = roles.get(i).getUser().getId().equals(userId) && roles.get(i).getRole().getName().equals(role.getName());
            i++;
        }

        return contains;
    }


}
