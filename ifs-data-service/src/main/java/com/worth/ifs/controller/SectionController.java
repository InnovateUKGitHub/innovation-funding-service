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
import com.worth.ifs.repository.SectionRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
    @Autowired
    SectionRepository sectionRepository;

    private final Log log = LogFactory.getLog(getClass());


    @RequestMapping("/getCompletedSections/{applicationId}")
    public Set<Long> getCompletedSectionsJson(@PathVariable("applicationId") final Long applicationId) {
        return this.getCompletedSections(applicationId);
    }
    private Set<Long> getCompletedSections(Long applicationId){
        Set<Long> completedSections = new LinkedHashSet<>();
        Application application = applicationRepository.findOne(applicationId);
        List<Section> sections = application.getCompetition().getSections();
        for (Section section : sections) {
            List<Section> childSections = section.getChildSections();
            for (Section childSection : childSections) {
                if(this.sectionIsComplete(childSection, applicationId)){
                    completedSections.add(childSection.getId());
                }
            }
            if(this.sectionIsComplete(section, applicationId)){
                completedSections.add(section.getId());
            }
        }
        List<Long> incomplete = this.getIncompleteSections(applicationId);

        completedSections=completedSections.stream()
                .filter(c -> !incomplete.contains(c))
                .collect(Collectors.toSet());

        return completedSections;
    }


    @RequestMapping("/getIncompleteSections/{applicationId}")
    public List<Long> getIncompleteSectionsJson(@PathVariable("applicationId") final Long applicationId) {
        return this.getIncompleteSections(applicationId);
    }

    @RequestMapping("findByName/{name}")
    public Section findByName(@PathVariable("name") final String name) {
        return sectionRepository.findByName(name);
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
    private boolean sectionIsComplete(Section section, Long applicationId){
        List<Question> questions = section.getQuestions();
        boolean sectionComplete = false;
        for (Question question : questions) {
            Response response = responseRepository.findByApplicationIdAndQuestionId(applicationId, question.getId());
            if(response != null && response.isMarkedAsComplete()){
                sectionComplete = true;
            }else{
                sectionComplete = false;
                break;
            }
        }
        return sectionComplete;
    }



}
