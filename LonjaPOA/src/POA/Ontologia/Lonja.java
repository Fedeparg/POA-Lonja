package POA.Ontologia;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import jade.core.AID;

public class Lonja {

	private LinkedList<AID> vendedores = new LinkedList<AID>();
	private HashMap<AID, Double> dineroCompradores = new HashMap<AID, Double>();
	private HashMap<Articulo, AID> articulosParaSubastar = new HashMap<Articulo, AID>();
	private HashMap<AID, LinkedList<Articulo>> articulosCompradosNoPagados = new HashMap<AID, LinkedList<Articulo>>();
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

	public HashMap<AID, LinkedList<Articulo>> getArticulosCompradosNoPagados() {
		return articulosCompradosNoPagados;
	}

	public void setArticulosCompradosNoPagados(HashMap<AID, LinkedList<Articulo>> articulosCompradosNoPagados) {
		this.articulosCompradosNoPagados = articulosCompradosNoPagados;
	}

	public void setArticulosParaSubastar(HashMap<Articulo, AID> articulosParaSubastar) {
		this.articulosParaSubastar = articulosParaSubastar;
	}

	public void addArticuloParaSubastar(Articulo articulo, AID vendedor) {
		articulosParaSubastar.put(articulo, vendedor);
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
		dineroCompradores.replace(comprador, dineroComprador - articulo.getPrecio());
		articulosParaSubastar.remove(articulo);
		synchronized (articulosCompradosNoPagados) {
			if (articulosCompradosNoPagados.containsKey(vendedor)) {
				LinkedList<Articulo> articulos = articulosCompradosNoPagados.get(vendedor);
				articulos.add(articulo);
				articulosCompradosNoPagados.replace(vendedor, articulos);
			} else {
				LinkedList<Articulo> articulos = new LinkedList<Articulo>();
				articulos.add(articulo);
				articulosCompradosNoPagados.put(vendedor, articulos);
			}
		}
	}

	public void imposibleVender(Articulo articulo) {
		AID vendedor = articulosParaSubastar.get(articulo);
		articulosParaSubastar.remove(articulo);
		articulosImposiblesDeVender.put(articulo, vendedor);
	}

	public double getDineroCobro(AID vendedor) {
		LinkedList<Articulo> ventas = articulosCompradosNoPagados.get(vendedor);
		Double dinero = 0.0;
		for (Articulo articulo : ventas) {
			dinero += articulo.getPrecio();
		}
		return dinero;
	}

	public void eliminarArticulosCobrados(AID vendedor, LinkedList<Articulo> articulos) {
		synchronized (articulosCompradosNoPagados) {
			LinkedList<Articulo> articulosVendedor = articulosCompradosNoPagados.get(vendedor);
			articulosVendedor.removeAll(articulos);
			if (articulosVendedor.isEmpty()) {
				articulosCompradosNoPagados.remove(vendedor);
			} else {
				articulosCompradosNoPagados.replace(vendedor, articulosVendedor);
			}
		}
	}
}
