package br.edu.faculdadedelta.modelo;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class BaseEntity<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 7373447920782854276L;

	@Version
	private Integer version;

	public abstract T getId();

	/**
	 * Obtém a versão da entidade
	 * 
	 * @return
	 */
	public Integer getVersion() {
		return version;
	}
	
	/**
	 * Metodo acessório para vericar se já tem identificador na entidade
	 * 
	 * @return
	 */
	public boolean isTransient(){
		return getId() == null;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		@SuppressWarnings("rawtypes")
		BaseEntity other = (BaseEntity) obj;
		if (getId() == null) {

			if (other.getId() != null)
				return false;

		} else if (!getId().equals(other.getId()))
			return false;

		return true;
	}
}