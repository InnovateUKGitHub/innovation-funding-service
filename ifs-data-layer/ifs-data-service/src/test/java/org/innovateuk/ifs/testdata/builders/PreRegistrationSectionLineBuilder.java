package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.testdata.builders.data.PreRegistrationSectionLine;

public class PreRegistrationSectionLineBuilder {

    public String competitionName;
    public String sectionName;
    public String subSectionName;
    public String questionName;

    public PreRegistrationSectionLineBuilder() {
    }

    public static PreRegistrationSectionLineBuilder aPreRegistrationSectionLine() {
        return new PreRegistrationSectionLineBuilder();
    }

    public PreRegistrationSectionLineBuilder withCompetitionName(String competitionName) {
        this.competitionName = competitionName;
        return this;
    }

    public PreRegistrationSectionLineBuilder withSectionName(String sectionName) {
        this.sectionName = sectionName;
        return this;
    }

    public PreRegistrationSectionLineBuilder withSubSectionName(String subSectionName) {
        this.subSectionName = subSectionName;
        return this;
    }

    public PreRegistrationSectionLineBuilder withQuestionName(String questionName) {
        this.questionName = questionName;
        return this;
    }

    public PreRegistrationSectionLine build() {
        PreRegistrationSectionLine preRegistrationSectionLine = new PreRegistrationSectionLine();

        preRegistrationSectionLine.setCompetitionName(competitionName);
        preRegistrationSectionLine.setSectionName(sectionName);
        preRegistrationSectionLine.setSubSectionName(subSectionName);
        preRegistrationSectionLine.setQuestionName(questionName);

        return preRegistrationSectionLine;
    }
}
