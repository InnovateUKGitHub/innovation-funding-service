package com.worth.ifs.competition.resource;

public enum LeadApplicantType {
	BUSINESS("business", "Business"), RESEARCH("research", "Research"), EITHER("either", "Either");

	private String code;
	private String name;
	
	private LeadApplicantType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static LeadApplicantType fromCode(String code) {
		for(LeadApplicantType type: LeadApplicantType.values()){
			if(type.getCode().equals(code)){
				return type;
			}
		}
		return null;
	}
}
