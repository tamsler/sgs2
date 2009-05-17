package org.sakaiproject.sgs2.server;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.sakaiproject.sgs2.client.model.Script;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GroovyShellManagerImpl extends HibernateDaoSupport implements GroovyShellManager {

	public void save(Script script) {
		getHibernateTemplate().save(script);
	}
	
	public void delete(Script script) {
		getHibernateTemplate().delete(script);
	}
	
	public Collection<Script> getScripts(String userEid) {
		DetachedCriteria d = DetachedCriteria.forClass(Script.class)
		.add( Restrictions.eq("userEid", userEid) )
		.addOrder(Order.desc("executionDate") );
		return getHibernateTemplate().findByCriteria(d);
	}
}
