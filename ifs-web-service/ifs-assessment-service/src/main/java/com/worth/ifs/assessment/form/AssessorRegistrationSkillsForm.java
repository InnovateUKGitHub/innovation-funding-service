package com.worth.ifs.assessment.form;

public class AssessorRegistrationSkillsForm {

    private String skillAreas;
    private String assessorType;

    public AssessorRegistrationSkillsForm() {
    }

    public AssessorRegistrationSkillsForm(String skillAreas, String assessorType) {
        this.skillAreas = skillAreas;
        this.assessorType = assessorType;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public void setSkillAreas(String skillAreas) {
        this.skillAreas = skillAreas;
    }

    public String getAssessorType() {
        return assessorType;
    }

    public void setAssessorType(String assessorType) {
        this.assessorType = assessorType;
    }
}
