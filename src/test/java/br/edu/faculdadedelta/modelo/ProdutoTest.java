package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.TypedQuery;

import org.junit.AfterClass;
import org.junit.Test;

import br.edu.faculdadedelta.base.BaseJPATest;

public class ProdutoTest extends BaseJPATest {

	@Test
	public void deveSalvarProduto() {

		Produto produto = new Produto("Notebook", "Dell");

		assertTrue("Não deve ter ID definido", produto.isTransient());

		getEntityManager().getTransaction().begin();
		getEntityManager().persist(produto);
		getEntityManager().getTransaction().commit();

		assertFalse("Deve ter definido", produto.isTransient());
		assertNotNull("Deve ter ID definido", produto.getId());
	}

	@Test
	public void devePesquisarProdutos() {

		IntStream.range(0, 10).forEach(i -> deveSalvarProduto());

		TypedQuery<Produto> query = getEntityManager().createQuery(" SELECT p FROM Produto p", Produto.class);
		List<Produto> produtos = query.getResultList();

		assertFalse("Deve ter produtos na lista", produtos.isEmpty());
		assertTrue("Deve ter produtos na lista", produtos.size() > 0);
	}

	@Test
	public void deveAlterarProduto() {

		deveSalvarProduto();

		TypedQuery<Produto> query = getEntityManager().createQuery(" SELECT p FROM Produto p", Produto.class)
				.setMaxResults(1);

		Produto produto = query.getSingleResult();

		assertNotNull("Dever ter encontrado produto", produto);

		Integer versao = produto.getVersion();

		getEntityManager().getTransaction().begin();

		produto.setFabricante("HP");
		produto = getEntityManager().merge(produto);
		getEntityManager().getTransaction().commit();

		assertNotEquals("Versão deve ser diferente", versao.intValue(), produto.getVersion().intValue());
	}

	@Test
	public void deveExcluirProduto() {

		deveSalvarProduto();

		TypedQuery<Long> query = getEntityManager().createQuery("SELECT MAX(p.id) FROM Produto p", Long.class);
		Long id = query.getSingleResult();

		getEntityManager().getTransaction().begin();

		Produto produto = getEntityManager().find(Produto.class, id);
		getEntityManager().remove(produto);

		getEntityManager().getTransaction().commit();

		Produto produtoExcluido = getEntityManager().find(Produto.class, id);

		assertNull("Não deve achar o produto", produtoExcluido);
	}

	
	@AfterClass
	public static void deveLimparBase() {
		
		deveLimparBase(Produto.class);
	}
}