package org.innovateuk.ifs.project.util;

import org.springframework.stereotype.Component;

@Component
public class FinanceUtil {

    public static final String RESEARCH_ORGANISATIONS = "Research";

    public boolean isUsingJesFinances(String organisationType) {
        switch(organisationType) {
            case RESEARCH_ORGANISATIONS:
                return true;
            default:
                return false;
        }
    }
}
