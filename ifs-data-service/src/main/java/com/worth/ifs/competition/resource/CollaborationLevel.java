package com.worth.ifs.competition.resource;

/**
 * This enum defines the options for the level of collaboration for a competition.
 */
public enum CollaborationLevel {
	SINGLE("single", "Single"), COLLABORATIVE("collaborative", "Collaborative"), SINGLE_OR_COLLABORATIVE("single-or-collaborative", "Single or Collaborative");

	private String code;
	private String name;
	
	private CollaborationLevel(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static CollaborationLevel fromCode(String code) {
		for(CollaborationLevel level: CollaborationLevel.values()){
			if(level.getCode().equals(code)){
				return level;
			}
		}
		return null;
	}
}
