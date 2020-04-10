package poa.ontologia;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import jade.core.AID;

/**
 * Representa la lonja y mantiene toda la información necesaria para el
 * mismo.
 * 
 */
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

	/**
	 * Devuelve una lista ordenada con los articulos listos para ser subastados. El
	 * orden es decidido mediante la hora de registro en la lonja, siendo el primero
	 * registrado el que aparece primero.
	 * 
	 * @return Lista ordenada mediante la hora de registro del articulo
	 */
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

	/**
	 * Este metodo es llamado cada vez que un artículo es vendido. Resta el dinero
	 * al comprador y procesa el articulo para mantener un seguimiento del mismo.
	 * 
	 * @param articulo  que se ha vendido
	 * @param comprador que ha comprado el articulo
	 */
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

	/**
	 * Elimina el articulo que no ha podido venderse de la lista de articulos para
	 * subastar y lo almacena en una lista propia para realizar un seguimiento del
	 * mismo
	 * 
	 * @param articulo que no ha podido venderse
	 */
	public void imposibleVender(Articulo articulo) {
		AID vendedor = articulosParaSubastar.get(articulo);
		articulosParaSubastar.remove(articulo);
		articulosImposiblesDeVender.put(articulo, vendedor);
	}

	/**
	 * Devuelve el dinero TOTAL de todos los articulos pertenecientes al vendedor
	 * que se han vendido.
	 * 
	 * @param vendedor del que queremos obtener el total de ganancias
	 * @return la suma del dinero de todos los articulos vendidos
	 */
	public double getDineroCobro(AID vendedor) {
		LinkedList<Articulo> ventas = articulosCompradosNoPagados.get(vendedor);
		Double dinero = 0.0;
		for (Articulo articulo : ventas) {
			dinero += articulo.getPrecio();
		}
		return dinero;
	}

	/**
	 * Elimina los articulos ya cobrados y deja de hacerles un seguimiento, pues ya
	 * han sido vendidos y cobrados.
	 * 
	 * @param vendedor  cuyo articulo/s ya se ha/n cobrado
	 * @param articulos que ya han sido cobrados
	 */
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
