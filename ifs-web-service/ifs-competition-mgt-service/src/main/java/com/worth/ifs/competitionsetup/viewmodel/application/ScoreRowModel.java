package com.worth.ifs.competitionsetup.viewmodel.application;

import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.form.resource.FormInputResource;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * View viewmodel where all information is for editing questions
 */
public class ScoreRowModel {
    private Long id;
    private Integer start;
    private Integer end;
    private String justification;

    public ScoreRowModel() {
    }

    public ScoreRowModel(Long id, Integer start, Integer end, String justification) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.justification = justification;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
