package Ontologia;

import java.util.LinkedList;
import java.util.List;

import jade.core.AID;
import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Vendedor implements Serializable{

	private int id;
	private String nombre;
	private LinkedList<Articulo> productosParaVender;
	private LinkedList<Articulo> productosPendientesCobro;
	private AID[] lonjas;

	public Vendedor(int id, String nombre) {
		this.id = id;
		this.nombre = nombre;
		this.productosParaVender = new LinkedList<Articulo>();
		this.productosPendientesCobro = new LinkedList<Articulo>();
	}
	
	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public LinkedList<Articulo> getProductosParaVender() {
		return productosParaVender;
	}

	public LinkedList<Articulo> getProductosPendientesCobro() {
		return productosPendientesCobro;
	}

	public AID[] getLonjas() {
		return lonjas;
	}
	
	public void addArticuloParaVender(Articulo articulo) {
		if (articulo.getVendedor().equals(this)) {
			this.productosParaVender.add(articulo);
		}
	}

	

}
