package com.worth.ifs.assessment.resource;


import java.io.Serializable;
import java.util.Optional;

public class Feedback {
    private Id id = new Id();
    private Optional<String> value;
    private Optional<String> text;


    public Feedback setId(Id id) {
        this.id = id;
        return this;
    }

    public Long getResponseId() {
        return id.getResponseId();
    }

    public Feedback setResponseId(Long responseId) {
        id.setResponseId(responseId);
        return this;
    }

    public Long getAssessorUserId() {
        return id.getAssessorUserId();
    }

    public Feedback setAssessorUserId(Long assessorProcessRoleId) {
        id.setAssessorUserId(assessorProcessRoleId);
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

    public static class Id implements Serializable {
        private Long responseId;
        private Long assessorUserId;

        public Id() {
        	// no-arg constructor
        }

        public Id(Long responseId, Long assessorUserId) {
            this.responseId = responseId;
            this.assessorUserId = assessorUserId;
        }

        public Long getResponseId() {
            return responseId;
        }

        public Id setResponseId(Long responseId) {
            this.responseId = responseId;
            return this;
        }

        public Long getAssessorUserId() {
            return assessorUserId;
        }

        public Id setAssessorUserId(Long assessorUserId) {
            this.assessorUserId = assessorUserId;
            return this;
        }
    }
}
