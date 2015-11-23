package com.worth.ifs.security;


import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.user.domain.User;
import org.springframework.stereotype.Component;

@Component
@PermissionRules
public class FeedbackRules {


    @PermissionRule("READ")
    public boolean hasPermission1(Feedback dto, User user){
        return true;
    }

    @PermissionRule("UPDATE")
    public boolean hasPermission2(Feedback dto, User user){
        return false;
    }
}
