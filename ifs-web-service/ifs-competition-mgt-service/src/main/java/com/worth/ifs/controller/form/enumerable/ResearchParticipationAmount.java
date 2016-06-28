package com.worth.ifs.controller.form.enumerable;

/**
 * defines the possible values for max research participation amount when defining a competition.
 */
public enum ResearchParticipationAmount {
	
	THIRTY(1, 30, "30%"), FIFTY(2, 50, "50%"), HUNDRED(3, 100, "100%"), NONE(4, 0, "None");
	
	private Integer id;
	private Integer amount;
	private String name;
	
	private ResearchParticipationAmount(Integer id, Integer amount, String name) {
		this.id = id;
		this.amount = amount;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}
	
	public Integer getAmount() {
		return amount;
	}
	
	public String getName() {
		return name;
	}
	
	public static ResearchParticipationAmount fromId(Integer id) {
		for(ResearchParticipationAmount amount: ResearchParticipationAmount.values()){
			if(amount.getId().equals(id)) {
				return amount;
			}
		}
		return null;
	}

	public static ResearchParticipationAmount fromAmount(Integer numericAmount) {
		for(ResearchParticipationAmount amount: ResearchParticipationAmount.values()){
			if(amount.getAmount().equals(numericAmount)) {
				return amount;
			}
		}
		return null;
	}
}
