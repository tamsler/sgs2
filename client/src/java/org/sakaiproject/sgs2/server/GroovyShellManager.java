package org.sakaiproject.sgs2.server;

import java.util.Collection;

import org.sakaiproject.sgs2.client.model.Script;

public interface GroovyShellManager {

	public void save(Script script);
	public void delete(Script script);
	public Collection<Script> getScripts(String userEid);

}
