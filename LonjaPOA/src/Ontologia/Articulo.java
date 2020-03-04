package Ontologia;

import java.util.Date;

public class Articulo {

	//private int id;
	private Pescado pescado;
	private double kilos;
	private Vendedor vendedor;
	private double precioSalida;
	private double precioReserva;
	private double precioFinal;
	private Date horaRegistro;
	private Date horaVenta;
	
	public Pescado getPescado() {
		return pescado;
	}
	public double getKilos() {
		return kilos;
	}
	public Vendedor getVendedor() {
		return vendedor;
	}
	public double getPrecioSalida() {
		return precioSalida;
	}
	public double getPrecioReserva() {
		return precioReserva;
	}
	public double getPrecioFinal() {
		return precioFinal;
	}
	public Date getHoraRegistro() {
		return horaRegistro;
	}
	public Date getHoraVenta() {
		return horaVenta;
	}
	
}
