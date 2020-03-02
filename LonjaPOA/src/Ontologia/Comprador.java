package Ontologia;

import java.util.LinkedList;
import java.util.List;

public class Comprador {
	private int id;
	private LinkedList<ArticuloCompra> listaCompra; // Pescadico a comprar
	private LinkedList<Articulo> articulosComprados; // Pescadico comprao
	private LinkedList<Articulo> pendienteRetirada;
	private double dinero; // Dinero que lleva encima

	public Comprador(int id, List<ArticuloCompra> lista, double oros) {
		this.id = id;
		this.listaCompra = (LinkedList<ArticuloCompra>) lista;
		this.dinero = oros;
		this.articulosComprados = new LinkedList<Articulo>();
		this.pendienteRetirada = new LinkedList<Articulo>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

}
