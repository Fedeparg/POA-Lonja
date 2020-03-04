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
import jade.lang.acl.MessageTemplate;

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

		Behaviour registro = new Registro(this, 10000);
		addBehaviour(registro);

	}

	class Registro extends TickerBehaviour {

		public Registro(Agent a, long period) {
			super(a, period);
		}

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
					e.printStackTrace();
				}
				mensajeRegistro.setConversationId("RegistroVendedor");
				send(mensajeRegistro);
				System.out.println("Enviando peticion de registro a lonja " + lonja);
			}

			MessageTemplate msjRegistroVendedor = MessageTemplate.MatchConversationId("RegistroVendedor");
			ACLMessage msjRegistro = receive(msjRegistroVendedor);
			if (msjRegistro != null) {
				if (msjRegistro.getPerformative() == ACLMessage.INFORM) {
					String contenido = msjRegistro.getContent();
					System.out.println(contenido);
					this.stop();
				} else { // ACLMessage.FAIULURE
					String contenido = msjRegistro.getContent();
					System.out.println(contenido);
				}

			}

		}

	}

}
