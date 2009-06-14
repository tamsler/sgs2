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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.sakaiproject.sgs2.client.model.Script;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GroovyShellManagerImpl extends HibernateDaoSupport implements GroovyShellManager {

	private static final Log LOG = LogFactory.getLog(GroovyShellManagerImpl.class);
	
	public Long save(Script script) {
		return (Long) getHibernateTemplate().save(script);
	}
	
	public void update(Script script) {
		
		getHibernateTemplate().saveOrUpdate(script);
	}
	
	public void delete(Script script) {
		getHibernateTemplate().delete(script);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Script> getScripts(String userId) {
		DetachedCriteria d = DetachedCriteria.forClass(Script.class)
		.add(Restrictions.eq("userId", userId))
		.addOrder(Order.desc("actionDate"));
		return getHibernateTemplate().findByCriteria(d);
	}

	@SuppressWarnings("unchecked")
	public Script getLatestScript(String userId) {
		DetachedCriteria d = DetachedCriteria.forClass(Script.class)
		.add(Restrictions.eq("userId", userId))
		.addOrder(Order.desc("actionDate"));
		List<Script> scripts = getHibernateTemplate().findByCriteria(d);
		
		if(null != scripts && !scripts.isEmpty()) {
			
			return scripts.get(0);
		}
		else {
			return null;
		}
	}

	public Script getScript(String uuid) {
		return (Script) getHibernateTemplate().get(Script.class, new Long(uuid));
	}

	@SuppressWarnings("unchecked")
	public Script getScript(String userId, String name) {
		DetachedCriteria d = DetachedCriteria.forClass(Script.class)
		.add(Restrictions.eq("name", name))
		.add(Restrictions.eq("userId", userId))
		.addOrder(Order.desc("actionDate"));
		List<Script> scripts = getHibernateTemplate().findByCriteria(d);
		
		if(null != scripts && scripts.size() > 0) {
			
			return scripts.get(0);
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getFavorite(final String userId) {
		
		List<String> scriptNames =  (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
			
            public Object doInHibernate(final Session session) throws HibernateException, SQLException {
                final Query q = session.createQuery("select name from Script as S where S.userId = :userId and S.favorite = :favorite order by S.actionDate desc");
                q.setString("userId", userId);
                q.setBoolean("favorite", Boolean.TRUE);
                return q.list();
            }
        });

		if(null == scriptNames) {
			return new ArrayList<String>();
		}
		else {
			return scriptNames;
		}
	}
}
