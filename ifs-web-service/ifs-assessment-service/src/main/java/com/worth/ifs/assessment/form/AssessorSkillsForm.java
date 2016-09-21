package com.worth.ifs.assessment.form;

public class AssessorSkillsForm {

    private String skillAreas;
    private String assessorType;

    public AssessorSkillsForm() {
    }

    public AssessorSkillsForm(String skillAreas, String assessorType) {
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
