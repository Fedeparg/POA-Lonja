package Ontologia;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jade.content.onto.SerializableOntology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.Serializable;

public class Vendedor implements Serializable{

	private int id;
	private String nombre;
	private LinkedList<Articulo> productosVendidos;
	private LinkedList<Articulo> productosPendientesCobro;
	private AID[] lonjas;

	public Vendedor(int id, String nombre, List<Articulo> productosAVender) {
		this.id = id;
		this.nombre = nombre;
		this.productosVendidos = (LinkedList<Articulo>) productosAVender;
		this.productosPendientesCobro = new LinkedList<Articulo>();
	}
	
	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public LinkedList<Articulo> getProductosVendidos() {
		return productosVendidos;
	}

	public LinkedList<Articulo> getProductosPendientesCobro() {
		return productosPendientesCobro;
	}

	public AID[] getLonjas() {
		return lonjas;
	}

	

}
