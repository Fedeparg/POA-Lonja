package poa.ontologia;

import java.util.LinkedList;

import jade.core.AID;
import jade.util.leap.Serializable;

/**
 * Representa un vendedor y mantiene toda la información necesaria para el
 * mismo.
 * 
 */
@SuppressWarnings("serial")
public class Vendedor implements Serializable {

	private double ganancias;

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

	public double getGanancias() {
		return ganancias;
	}

	public void setGanancias(double ganancias) {
		this.ganancias = ganancias;
	}

}
