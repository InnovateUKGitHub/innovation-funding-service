package com.worth.ifs.application.resource.comparators;

import java.util.Comparator;

import com.worth.ifs.application.resource.ApplicationSummaryResource;

/**
 * Camparator, handling nulls, and using id if lead applicant is equal.
 */
public class ApplicationSummaryResourceLeadApplicantComparator extends DualFieldComparator<String, Long> implements Comparator<ApplicationSummaryResource> {

	@Override
	public int compare(ApplicationSummaryResource o1, ApplicationSummaryResource o2) {
		
		String o1LeadApplicant = o1.getLeadApplicant();
		String o2LeadApplicant = o2.getLeadApplicant();
		
		Long o1Id = o1.getId();
		Long o2Id = o2.getId();
		
		return compare(o1LeadApplicant, o2LeadApplicant, o1Id, o2Id);
	}

}