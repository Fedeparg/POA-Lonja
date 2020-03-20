package POA.Ontologia;

import java.util.HashMap;
import java.util.LinkedList;

import jade.core.AID;

public class Lonja {

	private HashMap<AID, Vendedor> vendedores = new HashMap<AID, Vendedor>();
	private HashMap<AID, Comprador> compradores = new HashMap<AID, Comprador>();
	private LinkedList<Articulo> articulosParaSubastar = new LinkedList<Articulo>();
	private LinkedList<Articulo> articulosCompradosNoPagados = new LinkedList<Articulo>();
	private LinkedList<Articulo> articulosImposiblesDeVender = new LinkedList<Articulo>();
	private int precioMinimo;

		
	public HashMap<AID, Vendedor> getVendedores() {
		return vendedores;
	}

	public void setVendedores(HashMap<AID, Vendedor> vendedores) {
		this.vendedores = vendedores;
	}

	public HashMap<AID, Comprador> getCompradores() {
		return compradores;
	}

	public void setCompradores(HashMap<AID, Comprador> compradores) {
		this.compradores = compradores;
	}

	public LinkedList<Articulo> getArticulosParaSubastar() {
		return articulosParaSubastar;
	}

	public void setArticulosParaSubastar(LinkedList<Articulo> articulosParaSubastar) {
		this.articulosParaSubastar = articulosParaSubastar;
	}

	public LinkedList<Articulo> getArticulosCompradosNoPagados() {
		return articulosCompradosNoPagados;
	}

	public void setArticulosCompradosNoPagados(LinkedList<Articulo> articulosCompradosNoPagados) {
		this.articulosCompradosNoPagados = articulosCompradosNoPagados;
	}

	public LinkedList<Articulo> getArticulosImposiblesDeVender() {
		return articulosImposiblesDeVender;
	}

	public void setArticulosImposiblesDeVender(LinkedList<Articulo> articulosImposiblesDeVender) {
		this.articulosImposiblesDeVender = articulosImposiblesDeVender;
	}

	public int getPrecioMinimo() {
		return precioMinimo;
	}

	public void setPrecioMinimo(int precioMinimo) {
		this.precioMinimo = precioMinimo;
	}
}
