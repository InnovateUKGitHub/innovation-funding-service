package com.worth.ifs;

import javax.validation.constraints.Min;

public class ApplicationSummaryQueryForm {

	@Min(1)
	private Integer page = 1;
	
	private String sort;
	
	private String tab;
	
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public String getTab() {
		return tab;
	}
	
	public void setTab(String tab) {
		this.tab = tab;
	}
}
