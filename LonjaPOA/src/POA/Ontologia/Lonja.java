package POA.Ontologia;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Lonja {

	private LinkedList<AID> vendedores = new LinkedList<AID>();
	private HashMap<AID, Double> dineroCompradores = new HashMap<AID, Double>();
	private HashMap<Articulo, AID> articulosParaSubastar = new HashMap<Articulo, AID>();
	private HashMap<Articulo, AID> articulosCompradosNoPagados = new HashMap<Articulo, AID>();
	private HashMap<Articulo, AID> articulosImposiblesDeVender = new HashMap<Articulo, AID>();
	private int decrementoPrecio;
	private int periodoLatencia;
	private int ventanaOportunidad;

	public HashMap<AID, Double> getDineroCompradores() {
		return dineroCompradores;
	}

	public void setDineroCompradores(HashMap<AID, Double> dineroCompradores) {
		this.dineroCompradores = dineroCompradores;
	}

	public int getPeriodoLatencia() {
		return periodoLatencia;
	}

	public void setPeriodoLatencia(int periodoLatencia) {
		this.periodoLatencia = periodoLatencia;
	}

	public int getVentanaOportunidad() {
		return ventanaOportunidad;
	}

	public void setVentanaOportunidad(int ventanaOportunidad) {
		this.ventanaOportunidad = ventanaOportunidad;
	}

	public LinkedList<AID> getVendedores() {
		return vendedores;
	}

	public void setVendedores(LinkedList<AID> vendedores) {
		this.vendedores = vendedores;
	}

	public LinkedList<AID> getCompradores() {
		return new LinkedList<AID>(dineroCompradores.keySet());
	}

	public Double getDineroComprador(AID comprador) {
		return dineroCompradores.get(comprador);
	}

	public void setDineroComprador(AID comprador, Double dinero) {
		this.dineroCompradores.put(comprador, dinero);
	}

	public LinkedList<Articulo> getArticulosParaSubastar() {
		LinkedList<Articulo> articulos = new LinkedList<Articulo>(articulosParaSubastar.keySet());
		articulos.sort(new Comparator<Articulo>() {
			@Override
			public int compare(Articulo arg0, Articulo arg1) {
				return arg0.getHoraRegistro().compareTo(arg1.getHoraRegistro());
			}
		});
		return articulos;
	}

	public void setArticulosParaSubastar(HashMap<Articulo, AID> articulosParaSubastar) {
		this.articulosParaSubastar = articulosParaSubastar;
	}

	public void addArticuloParaSubastar(Articulo articulo, AID vendedor) {
		articulosParaSubastar.put(articulo, vendedor);
	}

	public LinkedList<Articulo> getArticulosCompradosNoPagados() {
		return new LinkedList<Articulo>(articulosCompradosNoPagados.keySet());
	}

	public void setArticulosCompradosNoPagados(HashMap<Articulo, AID> articulosCompradosNoPagados) {
		this.articulosCompradosNoPagados = articulosCompradosNoPagados;
	}

	public LinkedList<Articulo> getArticulosImposiblesDeVender() {
		return new LinkedList<Articulo>(articulosImposiblesDeVender.keySet());
	}

	public void setArticulosImposiblesDeVender(HashMap<Articulo, AID> articulosImposiblesDeVender) {
		this.articulosImposiblesDeVender = articulosImposiblesDeVender;
	}

	public int getDecrementoPrecio() {
		return decrementoPrecio;
	}

	public void setDecrementoPrecio(int decrementoPrecio) {
		this.decrementoPrecio = decrementoPrecio;
	}

	public void articuloVendido(Articulo articulo, AID comprador) {
		AID vendedor = articulosParaSubastar.get(articulo);
		Double dineroComprador = dineroCompradores.get(comprador);
		dineroCompradores.replace(comprador, dineroComprador-articulo.getPrecio());
		articulosParaSubastar.remove(articulo);
		articulosCompradosNoPagados.put(articulo, vendedor);
	}
	
	public void imposibleVender(Articulo articulo) {
		AID vendedor = articulosParaSubastar.get(articulo);
		articulosParaSubastar.remove(articulo);
		articulosImposiblesDeVender.put(articulo, vendedor);
	}

}
