package poa.ontologia;

import java.util.Iterator;
import java.util.LinkedList;

import jade.util.leap.Serializable;

/**
 * Representa un comprador y mantiene toda la información necesaria para el
 * mismo.
 * 
 */
@SuppressWarnings("serial")
public class Comprador implements Serializable {
	private LinkedList<ArticuloCompra> listaCompra; // Pescadico a comprar
	private LinkedList<Articulo> articulosComprados = new LinkedList<Articulo>(); // Pescadico comprao
	private LinkedList<Articulo> pendienteRetirada = new LinkedList<Articulo>();
	private double dinero; // Dinero que lleva encima

	public LinkedList<ArticuloCompra> getListaCompra() {
		return listaCompra;
	}

	public void setListaCompra(LinkedList<ArticuloCompra> listaCompra) {
		this.listaCompra = listaCompra;
	}

	public LinkedList<Articulo> getArticulosComprados() {
		return articulosComprados;
	}

	public void setArticulosComprados(LinkedList<Articulo> articulosComprados) {
		this.articulosComprados = articulosComprados;
	}

	public double getDinero() {
		return dinero;
	}

	public void setDinero(double dinero) {
		this.dinero = dinero;
	}

	public LinkedList<Articulo> getPendienteRetirada() {
		return pendienteRetirada;
	}

	public void setPendienteRetirada(LinkedList<Articulo> pendienteRetirada) {
		this.pendienteRetirada = pendienteRetirada;
	}

	public void eliminarListaCompra(Articulo articuloComprado) {
		for (Iterator<ArticuloCompra> iterator = listaCompra.iterator(); iterator.hasNext();) {
			ArticuloCompra articulo = (ArticuloCompra) iterator.next();
			if (articulo.getPescado().equals(articuloComprado.getPescado())) {
				if (articulo.getKilos() <= articuloComprado.getKilos()) {
					pendienteRetirada.add(articuloComprado);
					listaCompra.remove(articulo);
				} else {
					articulo.setKilos(articulo.getKilos() - articuloComprado.getKilos());
				}
			}
		}

	}

	public void eliminarArticuloRetirada(Articulo articulo) {
		this.articulosComprados.add(articulo);
		this.pendienteRetirada.remove(articulo);
		this.dinero = this.dinero - articulo.getPrecio();
	}

}
