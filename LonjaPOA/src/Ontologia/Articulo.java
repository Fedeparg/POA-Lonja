package Ontologia;

import java.util.Date;

import jade.util.leap.Serializable;

public class Articulo implements Serializable{

	private final int id;
	private Pescado pescado;
	private double kilos;
	private double precioSalida;
	private double precioReserva;
	private double precioFinal;
	private Vendedor vendedor;
	private Date horaRegistro;
	private Date horaVenta;
	
	public Articulo (int id, Pescado pescado, double kilos, double precioSalida, Vendedor vendedor) {
		this.id = id;
		this.pescado = pescado;
		this.kilos = kilos;
		this.precioSalida = precioSalida;
		this.vendedor = vendedor;
	}
	
	// Getters and setters
	public Pescado getPescado() {
		return pescado;
	}
	public void setPescado(Pescado pescado) {
		this.pescado = pescado;
	}
	public double getKilos() {
		return kilos;
	}
	public void setKilos(double kilos) {
		this.kilos = kilos;
	}
	public double getPrecioSalida() {
		return precioSalida;
	}
	public void setPrecioSalida(double precioSalida) {
		this.precioSalida = precioSalida;
	}
	public double getPrecioReserva() {
		return precioReserva;
	}
	public void setPrecioReserva(double precioReserva) {
		this.precioReserva = precioReserva;
	}
	public double getPrecioFinal() {
		return precioFinal;
	}
	public void setPrecioFinal(double precioFinal) {
		this.precioFinal = precioFinal;
	}
	public Date getHoraRegistro() {
		return horaRegistro;
	}
	public void setHoraRegistro(Date horaRegistro) {
		this.horaRegistro = horaRegistro;
	}
	public Date getHoraVenta() {
		return horaVenta;
	}
	public void setHoraVenta(Date horaVenta) {
		this.horaVenta = horaVenta;
	}
	
	public Vendedor getVendedor() {
		return vendedor;
	}
	
	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}
	
	public int getID() {
		return id;
	}
	
	
}
