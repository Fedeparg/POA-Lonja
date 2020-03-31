package POA.Ontologia;

import java.util.Date;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class Articulo implements Serializable {

	private String pescado;
	private double kilos;
	private double precio;
	private double precioReserva;
	private Date horaRegistro;
	private Date horaVenta;

	public void setPescado(String pescado) {
		this.pescado = pescado;
	}

	public String getPescado() {
		return pescado;
	}

	public double getKilos() {
		return kilos;
	}

	public void setKilos(double kilos) {
		this.kilos = kilos;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precioSalida) {
		this.precio = precioSalida;
	}

	public double getPrecioReserva() {
		return precioReserva;
	}

	public void setPrecioReserva(double precioReserva) {
		this.precioReserva = precioReserva;
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
	

}
