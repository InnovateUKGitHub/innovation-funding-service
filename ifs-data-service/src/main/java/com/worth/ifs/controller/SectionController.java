package com.worth.ifs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.Question;
import com.worth.ifs.domain.Response;
import com.worth.ifs.domain.Section;
import com.worth.ifs.repository.ApplicationRepository;
import com.worth.ifs.repository.ResponseRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ApplicationController exposes Application data through a REST API.
 */
@RestController
@RequestMapping("/section")
public class SectionController {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ResponseRepository responseRepository;


    private final Log log = LogFactory.getLog(getClass());


    @RequestMapping("/getCompletedSections/{applicationId}")
    public List<Long> getCompletedSectionsJson(@PathVariable("applicationId") final Long applicationId) {
        return this.getCompletedSections(applicationId);
    }
    private List<Long> getCompletedSections(Long applicationId){
        List<Long> completedSections = new ArrayList<>();
        Application application = applicationRepository.findOne(applicationId);
        List<Section> sections = application.getCompetition().getSections();
        for (Section section : sections) {
            boolean sectionComplete = false;

            List<Question> questions = section.getQuestions();
            for (Question question : questions) {
                Response response = responseRepository.findByApplicationIdAndQuestionId(applicationId, question.getId());
                if(response != null && response.isMarkedAsComplete()){
                    sectionComplete = true;
                }else{
                    sectionComplete = false;
                    break;
                }
            }
            if(sectionComplete){
                completedSections.add(section.getId());
            }
        }
        List<Long> incomplete = this.getIncompleteSections(applicationId);

        completedSections=completedSections.stream()
                .filter(c -> !incomplete.contains(c))
                .collect(Collectors.toList());

        return completedSections;
    }


    @RequestMapping("/getIncompleteSections/{applicationId}")
    public List<Long> getIncompleteSectionsJson(@PathVariable("applicationId") final Long applicationId) {
        return this.getIncompleteSections(applicationId);
    }

    public List<Long> getIncompleteSections(Long applicationId) {
        Application application = applicationRepository.findOne(applicationId);

        List<Section> sections = application.getCompetition().getSections();
        List<Long> incompleteSections = new ArrayList<>();

        for (Section section : sections) {
            boolean sectionIncomplete = false;

            List<Question> questions = section.getQuestions();
            for (Question question : questions) {
                if(question.getWordCount() != null && question.getWordCount() > 0){
                    // there is a maxWordCount.
                    Response response = responseRepository.findByApplicationIdAndQuestionId(applicationId, question.getId());
                    if(response != null && response.getWordCountLeft() < 0){
                        // gone over the limit !
                        sectionIncomplete = true;
                        break;
                    }else{
                        sectionIncomplete = false;
                    }
                }else{
                    // no wordcount.
                    sectionIncomplete = false;
                }
            }
            if(sectionIncomplete){
                incompleteSections.add(section.getId());
            }
        }

        return incompleteSections;
    }



}
