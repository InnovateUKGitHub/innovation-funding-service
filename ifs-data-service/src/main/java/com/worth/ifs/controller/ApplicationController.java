package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.User;
import com.worth.ifs.domain.UserApplicationRole;
import com.worth.ifs.repository.ApplicationRepository;
import com.worth.ifs.repository.UserApplicationRoleRepository;
import com.worth.ifs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    UserRepository userRepository;

//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    @ResponseStatus(value = HttpStatus.NOT_FOUND)
//    public String handleException(Exception e) {
//        return e.getMessage();
//    }

    @RequestMapping("/id/{id}")
    public Application getApplicationById(@PathVariable("id") final Long id) {
        List<Application> apps = repository.findById(id);
        if(apps.size() == 0){
            return null;
        }else{
            return apps.get(0);
        }


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
}
