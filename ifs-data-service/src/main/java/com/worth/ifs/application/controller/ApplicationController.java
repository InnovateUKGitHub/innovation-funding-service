package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ApplicationStatusRepository;
import com.worth.ifs.application.service.ApplicationRestServiceImpl;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionsRepository;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ApplicationController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/application")
public class ApplicationController {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ProcessRoleRepository processRoleRepository;
    @Autowired
    ApplicationStatusRepository applicationStatusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionController questionController;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    OrganisationRepository organisationRepository;
    @Autowired
    CompetitionsRepository competitionRepository;


    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/id/{id}")
    public Application getApplicationById(@PathVariable("id") final Long id) {
        return applicationRepository.findOne(id);
    }

    @RequestMapping("/findAll")
     public List<Application> findAll() {
        List<Application> applications = applicationRepository.findAll();
        return applications;
    }

    @RequestMapping("/findByUser/{userId}")
    public List<Application> findByUserId(@PathVariable("userId") final Long userId) {
        User user = userRepository.findOne(userId);
        List<ProcessRole> roles =  processRoleRepository.findByUser(user);
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

        Application applicationDb = applicationRepository.findOne(id);
        HttpStatus status;

        if (applicationDb != null) {
            applicationDb.setName(application.getName());
            applicationDb.setDurationInMonths(application.getDurationInMonths());
            applicationDb.setStartDate(application.getStartDate());
            applicationRepository.save(applicationDb);

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
        Application application = applicationRepository.findOne(applicationId);
        List<Section> sections = application.getCompetition().getSections();
        List<Question> questions = sections.stream()
                .flatMap((s) -> s.getQuestions().stream())
                .filter((q -> q.isMarkAsCompletedEnabled()))
                .collect(Collectors.toList());

        List<ProcessRole> processRoles = application.getProcessRoles();
        Set<Organisation> organisations = processRoles.stream().map(p -> p.getOrganisation()).collect(Collectors.toSet());

        Long countMultipleStatusQuestionsCompleted = organisations.stream()
                .mapToLong(org -> questions.stream()
                        .filter(q -> q.hasMultipleStatuses() && questionController.isMarkedAsComplete(q, applicationId, org.getId())).count())
                .sum();
        Long countSingleStatusQuestionsCompleted = questions.stream()
                .filter(q -> !q.hasMultipleStatuses() && questionController.isMarkedAsComplete(q, applicationId, 0L)).count();
        Long countCompleted = countMultipleStatusQuestionsCompleted + countSingleStatusQuestionsCompleted;

        Long totalMultipleStatusQuestions = questions.stream().filter(q -> q.hasMultipleStatuses()).count() * organisations.size();
        Long totalSingleStatusQuestions = questions.stream().filter(q -> !q.hasMultipleStatuses()).count();

        Long totalQuestions = totalMultipleStatusQuestions + totalSingleStatusQuestions;
        log.info("Total questions" + totalQuestions);
        log.info("Total completed questions" + countCompleted);

        double percentageCompleted;
        if(questions.size() == 0){
            percentageCompleted = 0;
        }else{
            percentageCompleted = (100.0 / totalQuestions) * countCompleted;
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("completedPercentage", percentageCompleted);
        return node;
    }

    @RequestMapping(value="/updateApplicationStatus", method=RequestMethod.GET)
    public ResponseEntity<String> updateApplicationStatus(@RequestParam("applicationId") final Long id,
                                                          @RequestParam("statusId") final Long statusId){

        Application application = applicationRepository.findOne(id);
        ApplicationStatus applicationStatus = applicationStatusRepository.findOne(statusId);
        application.setApplicationStatus(applicationStatus);
        applicationRepository.save(application);


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

        List<ProcessRole> roles =  processRoleRepository.findByUser(user);
        List<Application> allApps= applicationRepository.findAll();
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

    @RequestMapping(value = "/createApplicationByName/{competitionId}/{userToken}", method = RequestMethod.POST)
    public Application createApplicationByApplicationNameForUserTokenAndCompetitionId(
            @PathVariable("competitionId") final Long competitionId,
            @PathVariable("userToken") final String userToken,
            @RequestBody JsonNode jsonObj) {

        List<User> users = userRepository.findByToken(userToken);
        User user = users.get(0);

        String applicationName = jsonObj.get("name").textValue();
        Application application = new Application();
        application.setName(applicationName);
        LocalDate currentDate = LocalDate.now();
        application.setStartDate(currentDate);

        List<ApplicationStatus> applicationStatusList = applicationStatusRepository.findByName(ApplicationStatusConstants.CREATED.getName());
        ApplicationStatus applicationStatus = applicationStatusList.get(0);

        application.setApplicationStatus(applicationStatus);
        application.setDurationInMonths(3L);

        List<Role> roles = roleRepository.findByName("leadapplicant");
        Role role = roles.get(0);

        Competition competition = competitionRepository.findOne(1L);
        Organisation organisation = organisationRepository.findOne(1L);
        ProcessRole processRole = new ProcessRole(user, application, role, organisation);

        List<ProcessRole> processRoles = new ArrayList<ProcessRole>();
        processRoles.add(processRole);

        application.setProcessRoles(processRoles);
        application.setCompetition(competition);

        applicationRepository.save(application);
        processRoleRepository.save(processRole);

        return application;
    }
    @RequestMapping("/initiateTest")
    public void initiateTest() {
        ApplicationRestServiceImpl applicationRestService = new ApplicationRestServiceImpl();
        applicationRestService.createApplication(1L, "123abc", "testapplicationname4");
    }

}
