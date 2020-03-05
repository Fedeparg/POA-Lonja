package Ontologia;

import java.util.LinkedList;
import java.util.List;

import jade.core.AID;
import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Vendedor implements Serializable{

	private int id;
	private String nombre;
	private LinkedList<Articulo> productosVendidos;
	private LinkedList<Articulo> productosPendientesCobro;
	private AID[] lonjas;

	public Vendedor(int id, String nombre, List<Articulo> productosAVender) {
		this.id = id;
		this.nombre = nombre;
		this.productosVendidos = (LinkedList<Articulo>) productosAVender;
		this.productosPendientesCobro = new LinkedList<Articulo>();
	}
	
	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public LinkedList<Articulo> getProductosVendidos() {
		return productosVendidos;
	}

	public LinkedList<Articulo> getProductosPendientesCobro() {
		return productosPendientesCobro;
	}

	public AID[] getLonjas() {
		return lonjas;
	}

	

}
