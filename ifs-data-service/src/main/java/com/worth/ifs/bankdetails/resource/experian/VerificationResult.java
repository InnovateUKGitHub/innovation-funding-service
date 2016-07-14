package com.worth.ifs.bankdetails.resource.experian;

import java.util.List;

public class VerificationResult {
    String personalDetailsScore;
    String addressScore;
    String companyNameScore;
    String regNumberScore;
    List<Condition> conditions;
}
