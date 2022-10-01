package org.innovateuk.ifs.questionnaire.response.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireQuestionForm {

    @NotNull(message = "{validation.questionnaire.answer.required}")
    private Long option;
    private Long questionResponseId;
}
