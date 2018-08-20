package br.edu.faculdadedelta.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.edu.faculdadedelta.base.BaseJPATest;

public class JPAUtilTest extends BaseJPATest {

	@Test
	public void deveTerInstanciaDoEntityManagerDefinida() {
		
		assertNotNull("instância do EntityManager não deve ser nula", getEntityManager());
	}
	
	@Test
	public void deveFecharEntityManager() {
	
		getEntityManager().close();
		
		assertFalse("instância do EntityManager deve estar fechada", getEntityManager().isOpen());
	}
	
	@Test
	public void deveAbrirUmaTransacao(){
		
		assertFalse("Transação deve estar fechada", getEntityManager().getTransaction().isActive());	
		
		getEntityManager().getTransaction().begin();
		
		assertTrue("Transação deve estar aberta", getEntityManager().getTransaction().isActive());
	}
}
