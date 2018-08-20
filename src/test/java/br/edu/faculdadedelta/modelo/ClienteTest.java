package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.LazyInitializationException;
import org.junit.AfterClass;
import org.junit.Test;

import br.edu.faculdadedelta.base.BaseJPATest;

public class ClienteTest extends BaseJPATest {

	private static final String CPF_PADRAO = "111.111.111-11";

	@Test
	public void deveSalvarCliente() {

		Cliente cliente = new Cliente("Flávio de Souza", CPF_PADRAO);
		assertTrue("Não deve ter id definido", cliente.isTransient());
		
		Cliente clienteSalvo = salvaCliente(cliente);
		
		assertFalse("Deve ter definido", clienteSalvo.isTransient());
		assertNotNull("Deve ter id definido", clienteSalvo.getId());
	}
	
	private Cliente salvaCliente() {
		
		return salvaCliente(null); 
	}
	
	private Cliente salvaCliente(Cliente cliente) {
		
		if(cliente == null)
			cliente = new Cliente("Fulano", "222.222.222-22");
	
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(cliente);
		getEntityManager().getTransaction().commit();
	
		return cliente;
	}
	
	private void salvaVenda(Cliente cliente) {
		
		getEntityManager().getTransaction().begin();
		
		Produto produto = new Produto("Teclado", "HP");
		getEntityManager().persist(produto);
		
		Venda venda = new Venda();
		venda.setCliente(cliente);
		venda.getProdutos().add(produto);
		venda.setDataHora(new Date());
		getEntityManager().persist(venda);
		
		getEntityManager().getTransaction().commit();
	}

	@Test(expected = LazyInitializationException.class)
	public void naoDeveAcessarAtributoLazyForaEscopoEntityManager() {

		Cliente clienteInserido = salvaCliente();
		salvaVenda(clienteInserido);
		Cliente cliente = getEntityManager().find(Cliente.class, clienteInserido.getId());

		assertNotNull("Verifica se encontrou um registro", cliente);
		
		getEntityManager().detach(cliente);
		cliente.getCompras().size();

		fail("deve disparar LazyInitializationException ao Acessar Atributo Lazy Fora do Escopo EntityManager");
	}

	@Test
	public void deveAcessarAtributoLazy() {

		Cliente clienteInserido = salvaCliente();
		Cliente cliente = getEntityManager().find(Cliente.class, clienteInserido.getId());

		assertNotNull("Verifica se encontrou um registro", cliente);
		assertNotNull("Lista lazy não deve ser nula", cliente.getCompras());
	}

	@Test(expected = NoResultException.class)
	public void naoDeveFuncionarSingleResultComNenhumRegistro() {

		deveSalvarCliente();
		deveSalvarCliente();

		Query query = getEntityManager().createQuery("SELECT c.id FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", "000.000.000-00");
		query.getSingleResult();

		fail("metodo getSingleResult deve desparar exception NoResultException");
	}

	@Test(expected = NonUniqueResultException.class)
	public void naoDeveFuncionarSingleResultComMuitosRegistros() {

		deveSalvarCliente();
		deveSalvarCliente();
		Query query = getEntityManager().createQuery("SELECT c.id FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		query.getSingleResult();

		fail("metodo getSingleResult deve desparar exception NonUniqueResultException");
	}

	@Test
	public void deveVerificarExistenciaCliente() {

		deveSalvarCliente();
		Query query = getEntityManager().createQuery("SELECT COUNT(c.id) FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		Long qtdResultado = (Long) query.getSingleResult();

		assertTrue("Verifica se há registros na lista", qtdResultado > 0L);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarIdNomeForeach() {
		deveSalvarCliente();
		Query query = getEntityManager().createQuery("SELECT c.id, c.nome FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);

		List<Object[]> resultado = query.getResultList();

		assertFalse("Verifica se há registros na lista", resultado.isEmpty());

		resultado.forEach(linha -> {
			assertTrue("Verifica que o cpf deve estar nulo", linha[0] instanceof Long);
			assertTrue("Verifica que o cpf deve estar nulo", linha[1] instanceof String);

			Cliente cliente = new Cliente((Long) linha[0], (String) linha[1]);

			assertNotNull("Verifica que o cliente não deve estar nulo", cliente);
		});

	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarIdNome() {
		deveSalvarCliente();

		Query query = getEntityManager().createQuery("SELECT c.id, c.nome FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);

		List<Object[]> resultado = query.getResultList();

		assertFalse("Verifica se há registros na lista", resultado.isEmpty());

		for (Object[] linha : resultado) {
			assertTrue("Verifica que o cpf deve estar nulo", linha[0] instanceof Long);
			assertTrue("Verifica que o cpf deve estar nulo", linha[1] instanceof String);

			Cliente cliente = new Cliente((Long) linha[0], (String) linha[1]);

			assertNotNull("Verifica que o cliente não deve estar nulo", cliente);
		}
	}

	@Test
	public void deveConsultarApenasIdNome() {

		deveSalvarCliente();

		Query query = getEntityManager()
				.createQuery("SELECT new Cliente(c.id, c.nome) FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);

		@SuppressWarnings("unchecked")
		List<Cliente> clientes = query.getResultList();

		assertFalse("Verifica se há registros na lista", clientes.isEmpty());

		clientes.forEach(cliente -> {
			assertNull("Verifica que o cpf deve estar nulo", cliente.getCpf());
		});
	}

	@Test
	public void deveConsultarClienteComIdNome() {
		deveSalvarCliente();

		Query query = getEntityManager()
				.createQuery("SELECT new Cliente(c.id, c.nome) FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);

		@SuppressWarnings("unchecked")
		List<Cliente> clientes = query.getResultList();

		assertFalse("Verifica se há registros na lista", clientes.isEmpty());

		for (Cliente cliente : clientes) {
			assertNull("Verifica que o cpf deve estar nulo", cliente.getCpf());

			cliente.setCpf(CPF_PADRAO);
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void deveConsultarCpf() {
		deveSalvarCliente();

		String filtro = "Flávio";

		Query query = getEntityManager().createQuery("SELECT c.cpf FROM Cliente c WHERE c.nome LIKE :nome");
		query.setParameter("nome", "%" + filtro + "%");

		List<String> listaCpf = query.getResultList();

		assertFalse("Deve possuir itens", listaCpf.isEmpty());
	}

	@AfterClass
	public static void deveLimparBase() {
		
		deveLimparBase(Cliente.class);
	}
}