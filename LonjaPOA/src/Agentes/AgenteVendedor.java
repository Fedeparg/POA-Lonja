package Agentes;

import java.io.IOException;
import Ontologia.Vendedor;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class AgenteVendedor extends Agent {

	private AID[] lonjas;
	private Vendedor vendedor;

	public void setup() {

		System.out.println("Se ha creado " + this.getLocalName());
		
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			vendedor = (Vendedor) args[0];
			Behaviour registro = new Registro(this, 10000);
			addBehaviour(registro);
		}

	}
	
	
	// TODO cambiar el tipo de behaviour porque cuando recibe el INFORM no lo lee hasta que toca el tick
	// Posible solucion: one shot que en caso de recibir refuse vuelve llama otra vez al metodo action?
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
					mensajeRegistro.setContentObject(vendedor);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mensajeRegistro.setConversationId("RegistroVendedor");
				send(mensajeRegistro);
				System.out.println(this.myAgent.getLocalName()+ ": Enviando peticion de registro a lonja " + lonja.getLocalName());
			}

			MessageTemplate msjRegistroVendedor = MessageTemplate.MatchConversationId("RegistroVendedor");
			ACLMessage msjRegistro = receive(msjRegistroVendedor);
			if (msjRegistro != null) {
				if (msjRegistro.getPerformative() == ACLMessage.INFORM) {
					System.out.println(this.myAgent.getLocalName()+ ": Recibido mensaje de aceptacion registro en  " + msjRegistro.getSender().getLocalName());
					String contenido = msjRegistro.getContent();
					System.out.println(contenido);
					this.stop();
				} else { // ACLMessage.FAIULURE
					System.out.println(this.myAgent.getLocalName()+ ": Recibido mensaje de fallo registro en  " + msjRegistro.getSender().getLocalName());
					String contenido = msjRegistro.getContent();
					System.out.println(contenido);
				}

			}

		}

	}
}
