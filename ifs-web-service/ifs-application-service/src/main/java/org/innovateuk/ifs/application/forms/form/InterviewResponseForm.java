package org.innovateuk.ifs.application.forms.form;

import org.springframework.web.multipart.MultipartFile;

public class InterviewResponseForm {

    private MultipartFile response;

    public MultipartFile getResponse() {
        return response;
    }

    public void setResponse(MultipartFile response) {
        this.response = response;
    }
}
