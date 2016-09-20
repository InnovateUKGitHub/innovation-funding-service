package com.worth.ifs.assessment.model;

import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AssessorRegistrationModelPopulator {
    @Autowired
    private CompetitionInviteRestService inviteRestService;
    public AssessorRegistrationViewModel populateModel(String inviteHash) {
        List<String> genderOptions = new ArrayList<String>() {{
            add("Female");
            add("Male");
            add("Prefer not to say");
            }};
        List<String> ethnicityOptions = new ArrayList<String>() {{
            add("White");
            add("Mixed / multiple ethnic groups");
            add("Asian / Asian British");
            add("Black / African / Caribbean");
            add("Black British");
            add("Other ethnic group");
            add("Prefer not to say");
        }};
        List<String> disabledOptions = new ArrayList<String>() {{
            add("Yes");
            add("No");
            add("Prefer not to say");
        }};

        String email = getAssociatedEmailFromInvite(inviteHash);

        return new AssessorRegistrationViewModel(email, genderOptions, ethnicityOptions, disabledOptions);
    }

    private String getAssociatedEmailFromInvite(String inviteHash) {
        RestResult<CompetitionInviteResource> invite = inviteRestService.getInvite(inviteHash);
        return invite.getOptionalSuccessObject().get().getEmail();
    }
}
