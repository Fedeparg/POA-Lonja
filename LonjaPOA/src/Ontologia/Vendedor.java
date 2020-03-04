package Ontologia;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

public class Vendedor extends Agent {

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
	
	public void setup() {
		
		addBehaviour(new TickerBehaviour(this, 10000) {
			
			@Override
			protected void onTick() {
				
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("lonja");
				sd.setType("lonja");
				template.addServices(sd);
				DFAgentDescription[] result;
				try {
					result = DFService.search(myAgent, template);
					lonjas = new AID[result.length];
					for (int i = 0; i < result.length; i++) {
						lonjas[i] = result[i].getName();
					}
				} catch (FIPAException e) {
					e.printStackTrace();
				}	
				
				for (AID lonja : lonjas) {
					ACLMessage mensajeRegistro = new ACLMessage(ACLMessage.REQUEST);
					mensajeRegistro.addReceiver(lonja);
					try {
						mensajeRegistro.setContentObject(this);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mensajeRegistro.setConversationId("RegistroVendedor");
					send(mensajeRegistro);
				}
				
				
			}	
			
			
		});
		
		
	}
	
}
