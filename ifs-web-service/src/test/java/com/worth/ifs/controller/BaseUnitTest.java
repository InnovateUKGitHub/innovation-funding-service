package com.worth.ifs.controller;

import com.worth.ifs.domain.*;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.security.UserAuthentication;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class BaseUnitTest {
    public MockMvc mockMvc;
    public User loggedInUser;
    public UserAuthentication loggedInUserAuthentication;

    @Mock
    TokenAuthenticationService tokenAuthenticationService;

    public List<Application> applications;
    public List<Section> sections;
    public List<Question> questions;
    public Competition competition;

    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    public void setup(){
        loggedInUser = new User(1L, "Nico Bijl", "email@email.nl", "password", "tokenABC", "image", new ArrayList());
        loggedInUserAuthentication = new UserAuthentication(loggedInUser);

        applications = new ArrayList<>();
        sections = new ArrayList<>();
        questions = new ArrayList<>();
    }

    public void loginDefaultUser(){
        when(tokenAuthenticationService.getAuthentication(any(HttpServletRequest.class))).thenReturn(loggedInUserAuthentication);
    }

    public void setupCompetition(){
        competition = new Competition(1L, "Competition x", "Description afds", new Date());
        sections.add(new Section(1L, competition, null, "Application details"));
        sections.add(new Section(2L, competition, null, "Scope (Gateway question)"));
        sections.add(new Section(3L, competition, null, "Business proposition (Q1 - Q4)"));
        sections.add(new Section(4L, competition, null, "Project approach (Q5 - Q8)"));
        sections.add(new Section(5L, competition, null, "Funding (Q9 - Q10)"));
        sections.add(new Section(6L, competition, null, "Finances"));

        Question q01 = new Question(1L, competition, sections.get(0), "Application details");
        sections.get(0).setQuestions(Arrays.asList(q01));

        Question q20 = new Question(20L, competition, sections.get(2), "1. What is the business opportunity that this project addresses?");
        Question q21 = new Question(21L, competition, sections.get(2), "2. What is the size of the market opportunity that this project might open up?");
        Question q22 = new Question(22L, competition, sections.get(2), "3. How will the results of the project be exploited and disseminated?");
        Question q23 = new Question(23L, competition, sections.get(2), "4. What economic, social and environmental benefits is the project expected to deliver?");
        sections.get(2).setQuestions(Arrays.asList(q20, q21, q22, q23));

        Question q30 = new Question(30L, competition, sections.get(3), "5. What technical approach will be adopted and how will the project be managed?");
        Question q31 = new Question(31L, competition, sections.get(3), "6. What is innovative about this project?");
        Question q32 = new Question(32L, competition, sections.get(3), "7. What are the risks (technical, commercial and environmental) to project success? What is the project's risk management strategy?");
        Question q33 = new Question(33L, competition, sections.get(3), "8. Does the project team have the right skills and experience and access to facilities to deliver the identified benefits?");
        sections.get(3).setQuestions(Arrays.asList(q30, q31, q32, q33));



        for (Section section : sections) {
            List<Question> sectionQuestions = section.getQuestions();
            if(sectionQuestions != null){
                questions.addAll(sectionQuestions);
            }
        }

        competition.setSections(sections);
        competition.setQuestions(questions);

    }

    public void setupApplicationWithRoles(){
        Application app1 = new Application(1L, "Rovel Additive Manufacturing Process", new ProcessStatus(1L, "created"));
        Application app2 = new Application(2L, "Providing sustainable childcare", new ProcessStatus(2L, "submitted"));
        Application app3 = new Application(3L, "Mobile Phone Data for Logistics Analytics", new ProcessStatus(3L, "approved"));
        Application app4 = new Application(4L, "Using natural gas to heat homes", new ProcessStatus(4L, "rejected"));
        Role role = new Role(1L, "leadapplicant", null);

        UserApplicationRole userAppRole1 = new UserApplicationRole(1L, loggedInUser, app1, role);
        UserApplicationRole userAppRole2 = new UserApplicationRole(2L, loggedInUser, app2, role);
        UserApplicationRole userAppRole3 = new UserApplicationRole(3L, loggedInUser, app3, role);
        UserApplicationRole userAppRole4 = new UserApplicationRole(4L, loggedInUser, app4, role);

        competition.addApplication(app1, app2, app3, app4);

        app1.setCompetition(competition);
        app1.setUserApplicationRoles(Arrays.asList(userAppRole1));
        app2.setCompetition(competition);
        app2.setUserApplicationRoles(Arrays.asList(userAppRole2));
        app3.setCompetition(competition);
        app3.setUserApplicationRoles(Arrays.asList(userAppRole3));
        app4.setCompetition(competition);
        app4.setUserApplicationRoles(Arrays.asList(userAppRole4));

        loggedInUser.addUserApplicationRole(userAppRole1, userAppRole2, userAppRole3, userAppRole3);
        applications = Arrays.asList(app1, app2, app3, app4);

    }
}
