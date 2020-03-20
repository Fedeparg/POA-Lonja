package POA.Ontologia;

import jade.util.leap.Serializable;

@SuppressWarnings("serial")
public class ArticuloCompra implements Serializable {

	private String pescado;
	private double kilos;
	
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
