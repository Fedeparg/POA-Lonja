package Ontologia;

import java.util.LinkedList;

import jade.core.AID;
import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Vendedor implements Serializable {

	private LinkedList<Articulo> productosParaVender;
	public void setProductosParaVender(LinkedList<Articulo> productosParaVender) {
		this.productosParaVender = productosParaVender;
	}

	public void setProductosPendientesCobro(LinkedList<Articulo> productosPendientesCobro) {
		this.productosPendientesCobro = productosPendientesCobro;
	}

	public void setLonjas(AID[] lonjas) {
		this.lonjas = lonjas;
	}

	private LinkedList<Articulo> productosPendientesCobro = new LinkedList<Articulo>();
	private AID[] lonjas;

	public LinkedList<Articulo> getProductosParaVender() {
		return productosParaVender;
	}

	public LinkedList<Articulo> getProductosPendientesCobro() {
		return productosPendientesCobro;
	}

	public AID[] getLonjas() {
		return lonjas;
	}

}
