package Ontologia;

public class ArticuloCompra {

	private Pescado pescado;
	private double kilos;
	
	public ArticuloCompra(Pescado pescado, double kg) {
		this.pescado = pescado;
		this.kilos = kg;
	}

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

}
