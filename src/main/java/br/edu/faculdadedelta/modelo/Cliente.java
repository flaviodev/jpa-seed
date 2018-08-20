package br.edu.faculdadedelta.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Cliente extends BaseEntity<Long> {

	private static final long serialVersionUID = 4586447920782856584L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_cliente")
	private Long id;
	
	@Column(length = 60, nullable = false)
	private String nome;
	
	@Column(length = 20)
	private String cpf;
	
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
	private List<Venda> compras;
	
	public Cliente() { }

	public Cliente(String nome, String cpf) {

		this.nome = nome;
		this.cpf = cpf;
	}

	public Cliente(Long id, String nome) {
		
		this.id = id;
		this.nome = nome;
	}

	@Override
	public Long getId() {
		
		return id;
	}

	public String getNome() {
		
		return nome;
	}

	public void setNome(String nome) {
		
		this.nome = nome;
	}

	public String getCpf() {
		
		return cpf;
	}

	public void setCpf(String cpf) {
		
		this.cpf = cpf;
	}

	public List<Venda> getCompras() {
		
		if(this.compras == null)
			this.compras = new ArrayList<>();
		
		return this.compras;
	}
}