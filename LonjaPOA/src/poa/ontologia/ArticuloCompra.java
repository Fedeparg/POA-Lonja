package poa.ontologia;

import jade.util.leap.Serializable;

/**
 * Version especial de Articulo (no relacionada) para el comprador. De esta
 * forma, el comprador no tiene acceso a información que no le es necesaria.
 * 
 */
@SuppressWarnings("serial")
public class ArticuloCompra implements Serializable {

	private String pescado;
	private double kilos;
	private double precioDispuesto;

	public double getPrecioDispuesto() {
		return precioDispuesto;
	}

	public void setPrecioDispuesto(double precioDispuesto) {
		this.precioDispuesto = precioDispuesto;
	}

	public String getPescado() {
		return pescado;
	}

	public void setPescado(String pescado) {
		this.pescado = pescado;
	}

	public double getKilos() {
		return kilos;
	}

	public void setKilos(double kilos) {
		this.kilos = kilos;
	}

}
