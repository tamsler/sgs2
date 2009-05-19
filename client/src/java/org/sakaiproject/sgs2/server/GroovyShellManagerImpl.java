/**********************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sgs2.server;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.sakaiproject.sgs2.client.model.Script;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GroovyShellManagerImpl extends HibernateDaoSupport implements GroovyShellManager {

	public Long save(Script script) {
		return (Long) getHibernateTemplate().save(script);
	}
	
	public void update(Script script) {
		
		getHibernateTemplate().saveOrUpdate(script);
	}
	
	public void delete(Script script) {
		getHibernateTemplate().delete(script);
	}
	
	public Collection<Script> getScripts(String userId) {
		DetachedCriteria d = DetachedCriteria.forClass(Script.class)
		.add(Restrictions.eq("userId", userId))
		.addOrder(Order.desc("actionDate"));
		return getHibernateTemplate().findByCriteria(d);
	}

	public Script getLatestScript(String userId) {
		DetachedCriteria d = DetachedCriteria.forClass(Script.class)
		.add(Restrictions.eq("userId", userId))
		.addOrder(Order.desc("actionDate"));
		List<Script> scripts = getHibernateTemplate().findByCriteria(d);
		
		if(!scripts.isEmpty()) {
			
			return scripts.get(0);
		}
		else {
			return null;
		}
	}
}
