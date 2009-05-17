package org.sakaiproject.sgs2.client.model;

import java.io.Serializable;
import java.util.Date;

public class Script implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String script;
	private String userEid;
	private Date executionDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getUserEid() {
		return userEid;
	}
	public void setUserEid(String userEid) {
		this.userEid = userEid;
	}
	public Date getExecutionDate() {
		return executionDate;
	}
	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}
}
