package com.worth.ifs.service;

import org.springframework.stereotype.Service;

@Service
public class ApplicationSummarySortFieldService {

	public String sortFieldForOpenCompetition(String sort) {
    	return activeSortField(sort, "percentageComplete", "id", "lead", "name");
	}
    
	public String sortFieldForSubmittedApplications(String sort) {
		return activeSortField(sort,  "id", "lead", "name", "numberOfPartners", "grantRequested", "totalProjectCost", "duration");
	}

	public String sortFieldForNotSubmittedApplications(String sort) {
		return activeSortField(sort, "percentageComplete", "id", "lead", "name");
	}

	private String activeSortField(String givenField, String defaultField, String... allowedFields) {
		for(String allowedField: allowedFields) {
			if(allowedField.equals(givenField)) {
				return givenField;
			}
		}
		return defaultField;
	}

}
