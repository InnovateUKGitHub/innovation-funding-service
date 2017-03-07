package org.innovateuk.ifs.project.util;

import org.springframework.stereotype.Component;

@Component
public class FinanceUtil {

    public static final String UNIVERSITY_HEI = "University (HEI)";

    public boolean isUsingJesFinances(String organisationType) {
        switch(organisationType) {
            case UNIVERSITY_HEI:
                return true;
            default:
                return false;
        }
    }
}
