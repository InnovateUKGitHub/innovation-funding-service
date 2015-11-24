package com.worth.ifs.assessment.dto;


import java.util.Optional;

public class Feedback {
    private Long responseId;
    private Long assessorProcessRoleId;
    private Optional<String> value;
    private Optional<String> text;

    public Long getResponseId() {
        return responseId;
    }

    public Feedback setResponseId(Long responseId) {
        this.responseId = responseId;
        return this;
    }

    public Long getAssessorProcessRoleId() {
        return assessorProcessRoleId;
    }

    public Feedback setAssessorProcessRoleId(Long assessorProcessRoleId) {
        this.assessorProcessRoleId = assessorProcessRoleId;
        return this;
    }

    public Optional<String> getValue() {
        return value;
    }

    public Feedback setValue(Optional<String> value) {
        this.value = value;
        return this;
    }

    public Optional<String> getText() {
        return text;
    }

    public Feedback setText(Optional<String> text) {
        this.text = text;
        return this;
    }
}
