package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.stream.IntStream;

import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.Test;

import br.edu.faculdadedelta.base.BaseJPATest;

public class VendaTest extends BaseJPATest {

	private static final String CPF_PADRAO = "111.111.111-11";

	@Test
	public void deveConsultarQuantidadeDeProdutosVendidos() {

		Venda venda = criarVenda("010.020.030-04");

		IntStream.range(0, 10).forEach(i -> venda.getProdutos().add(new Produto("Produto" + i, "Marca" + i)));

		getEntityManager().getTransaction().begin();
		getEntityManager().persist(venda);
		getEntityManager().getTransaction().commit();

		assertFalse("Deve ter persistido a venda", venda.isTransient());

		int qtdProdutosAdicionados = venda.getProdutos().size();

		assertTrue("Lista de produtos deve ter itens", qtdProdutosAdicionados > 0);

		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(p.id) ");
		jpql.append(" FROM Venda v ");
		jpql.append(" INNER JOIN v.produtos p ");
		jpql.append(" INNER JOIN v.cliente c ");
		jpql.append(" WHERE c.cpf = :cpf ");

		Query query = getEntityManager().createQuery(jpql.toString());
		query.setParameter("cpf", "010.020.030-04");

		Long qtdProdutosDaVenda = (Long) query.getSingleResult();

		assertEquals("quantidade de produtos deve ser igual a quantidade da lista", qtdProdutosDaVenda.intValue(),
				qtdProdutosAdicionados);
	}

	@Test(expected = IllegalStateException.class)
	public void naoDeveFazerMergeEmObjetosTransient() {

		Venda venda = criarVenda();

		venda.getProdutos().add(new Produto("Notebook", "Dell"));
		venda.getProdutos().add(new Produto("Mouse", "Razer"));

		assertTrue("Não deve ter id definido", venda.isTransient());

		getEntityManager().getTransaction().begin();
		venda = getEntityManager().merge(venda);
		getEntityManager().getTransaction().commit();

		fail("Não deveria ter salvo (merge) uma venda nova com relacionamentos transient");
	}

	@Test
	public void deveSalvarVendaComRelacionamentosEmCascataForeach() {

		Venda venda = criarVenda();

		venda.getProdutos().add(new Produto("Notebook", "Dell"));
		venda.getProdutos().add(new Produto("Mouse", "Razer"));

		assertTrue("Não deve ter id definido", venda.isTransient());

		getEntityManager().getTransaction().begin();
		getEntityManager().persist(venda);
		getEntityManager().getTransaction().commit();

		assertFalse("Deve ter id definido", venda.isTransient());
		assertFalse("Deve ter id definido", venda.getCliente().isTransient());

		venda.getProdutos().forEach(produto -> assertFalse("Deve ter id definido", produto.isTransient()));
	}

	@Test
	public void deveSalvarVendaComRelacionamentosEmCascata() {

		Venda venda = criarVenda();

		venda.getProdutos().add(new Produto("Notebook", "Dell"));
		venda.getProdutos().add(new Produto("Mouse", "Razer"));

		assertTrue("Não deve ter id definido", venda.isTransient());

		getEntityManager().getTransaction().begin();
		getEntityManager().persist(venda);
		getEntityManager().getTransaction().commit();

		assertFalse("Deve ter id definido", venda.isTransient());
		assertFalse("Deve ter id definido", venda.getCliente().isTransient());

		venda.getProdutos().forEach(produto -> assertFalse("Deve ter id definido", produto.isTransient()));
	}

	private Venda criarVenda() {

		return criarVenda(null);
	}

	private Venda criarVenda(String cpf) {

		Cliente cliente = new Cliente("Flavio de Souza", cpf == null ? CPF_PADRAO : cpf);
		assertTrue("Não deve ter id definido", cliente.isTransient());

		Venda venda = new Venda();
		venda.setDataHora(new Date());
		venda.setCliente(cliente);

		return venda;
	}

	@AfterClass
	public static void deveLimparBase() {

		deveLimparBase(Venda.class);
		deveLimparBase(Cliente.class);
		deveLimparBase(Produto.class);
	}
}