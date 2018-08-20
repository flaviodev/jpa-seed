package br.edu.faculdadedelta.base;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

import br.edu.faculdadedelta.modelo.BaseEntity;
import br.edu.faculdadedelta.util.JPAUtil;

public class BaseJPATest {

	private EntityManager em;

	@Before
	public void instanciarEntityManager() {

		em = JPAUtil.INSTANCE.getEntityManager();
	}

	@After
	public void fecharEntityManager() {

		if (em.isOpen())
			em.close();
	}

	protected EntityManager getEntityManager() {

		return this.em;
	}

	public static <ID extends Serializable, E extends BaseEntity<ID>> void deveLimparBase(Class<E> classeEntidade) {

		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();

		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM " + classeEntidade.getSimpleName());
		query.executeUpdate();
		entityManager.getTransaction().commit();

		query = entityManager.createQuery("SELECT e FROM " + classeEntidade.getSimpleName() + " e");
		int totalRequistros = query.getResultList().size();
		assertTrue("Tabela não deve possuir registros", totalRequistros == 0);
	}

	protected <ID extends Serializable, E extends BaseEntity<ID>> Criteria createCriteria(Class<E> clazz,
			String alias) {

		return ((Session) getEntityManager().getDelegate()).createCriteria(clazz, alias);
	}

}
