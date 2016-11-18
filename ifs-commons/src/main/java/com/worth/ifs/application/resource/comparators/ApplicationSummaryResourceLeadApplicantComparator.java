package com.worth.ifs.application.resource.comparators;

import java.util.Comparator;

import com.worth.ifs.application.resource.ApplicationSummaryResource;

/**
 * Camparator, handling nulls, and using id if lead applicant is equal.
 */
public class ApplicationSummaryResourceLeadApplicantComparator extends DualFieldComparator<String, Long> implements Comparator<ApplicationSummaryResource> {

	@Override
	public int compare(ApplicationSummaryResource resource1, ApplicationSummaryResource resource2) {
		
		String o1LeadApplicant = resource1.getLeadApplicant();
		String o2LeadApplicant = resource2.getLeadApplicant();
		
		Long o1Id = resource1.getId();
		Long o2Id = resource2.getId();
		
		return compare(o1LeadApplicant, o2LeadApplicant, o1Id, o2Id);
	}

}