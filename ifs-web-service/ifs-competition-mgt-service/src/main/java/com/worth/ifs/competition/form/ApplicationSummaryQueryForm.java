package com.worth.ifs.competition.form;

import javax.validation.constraints.Min;

public class ApplicationSummaryQueryForm {

	@Min(value=1, message="{validation.applicationsummaryqueryform.page.min}")
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
