package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.form.builder.QuestionResourceBuilder;

import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;

public class QuestionDocs {

    public static final QuestionResourceBuilder questionBuilder = newQuestionResource()
            .withId(1L)
            .withName("question name")
            .withShortName("name")
            .withDescription("description")
            .withPriority(1)
            .withSection(1L)
            .withQuestionNumber("1")
            .withAssessorMaximumScore(10);
}
