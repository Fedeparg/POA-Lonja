package Ontologia;

import java.util.LinkedList;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class Lonja extends Agent {

	private LinkedList<Vendedor> vendedores;
	private LinkedList<Comprador> compradores;

	public Lonja() {
		this.vendedores = new LinkedList<Vendedor>();
		this.compradores = new LinkedList<Comprador>();
	}

	public void setup() {

		// Registrar el servicio en las paginas amarillas
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("lonja");
		sd.setName("lonja");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// Registro vendedor
		addBehaviour(new AdmisionVendedor());

	}

	class AdmisionVendedor extends Behaviour {

		Vendedor vendedor = null;

		@Override
		public void action() {

			// Recibimos la peticion de registro del vendedor
			MessageTemplate msjRegistroVendedor = MessageTemplate.MatchConversationId("RegistroVendedor");
			ACLMessage msjRegistro = receive(msjRegistroVendedor);
			if (msjRegistro != null) {
				try {
					vendedor = (Vendedor) msjRegistro.getContentObject();
				} catch (UnreadableException e) {
					System.out.println("Fallo al sacar el vendedor del mensaje de registro");
					e.printStackTrace();
				}
				// Añadimos el vendedor a lista de vendedores
				if (vendedor != null && !vendedores.contains(vendedor))
					vendedores.add(vendedor);
			}
		}

		@Override
		public boolean done() {
			for (Vendedor vendedor : vendedores) {
				System.out.println(vendedor.toString());
			}
			if (vendedores.contains(vendedor)) { // Se ha añadido el vendedor
				ACLMessage registroVendedorExito = new ACLMessage(ACLMessage.INFORM);
				registroVendedorExito.setConversationId("RegistroVendedor");
				registroVendedorExito.setContent("Registrado correctamente");
				send(registroVendedorExito);
				return true;
			} else { // No se ha añadido el vendedor
				ACLMessage registroVendedor = new ACLMessage(ACLMessage.FAILURE);
				registroVendedor.setConversationId("RegistroVendedor");
				registroVendedor.setContent("Fallo en el registro");
				send(registroVendedor);
				return false;
			}

		}
	};
}
