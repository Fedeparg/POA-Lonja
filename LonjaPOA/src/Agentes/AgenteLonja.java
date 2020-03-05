package Agentes;

import java.util.LinkedList;

import Ontologia.Comprador;
import Ontologia.Vendedor;
import Protocolos.AdmisionVendedorP;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class AgenteLonja extends Agent {

	private LinkedList<Vendedor> vendedores;
	private LinkedList<Comprador> compradores;

	public AgenteLonja() {
		this.vendedores = new LinkedList<Vendedor>();
		this.compradores = new LinkedList<Comprador>();
	}

	public void setup() {

		System.out.println("Se ha creado " + this.getLocalName());
		
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
		//addBehaviour(new AdmisionVendedor());
//		MessageTemplate msjRegistroVendedor = MessageTemplate.MatchConversationId("RegistroVendedor");
		MessageTemplate msjRegistroVendedor = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		addBehaviour(new AdmisionVendedorP(this, msjRegistroVendedor));
	}

	@SuppressWarnings("serial")
	/*class AdmisionVendedor extends Behaviour {

		Vendedor vendedor = null;

		@Override
		public void action() {

			// Recibimos la peticion de registro del vendedor
			MessageTemplate msjRegistroVendedor = MessageTemplate.MatchConversationId("RegistroVendedor");
			ACLMessage msjRegistro = receive(msjRegistroVendedor);
			if (msjRegistro != null) {
				System.out.println(this.getAgent().getLocalName() + ": Recibida petici�n de registro vendedor de " + msjRegistro.getSender().getLocalName());
				try {
					vendedor = (Vendedor) msjRegistro.getContentObject();
				} catch (UnreadableException e) {
					System.out.println("Fallo al sacar el vendedor del mensaje de registro");
					e.printStackTrace();
				}
				// A�adimos el vendedor a lista de vendedores
				if (vendedor != null && !vendedores.contains(vendedor)) {
					System.out.println(this.getAgent().getLocalName() + ": A�adido vendedor " + msjRegistro.getSender().getLocalName());
					vendedores.add(vendedor);
					ACLMessage registroVendedorExito = new ACLMessage(ACLMessage.INFORM);
					registroVendedorExito.setConversationId("RegistroVendedor");
					registroVendedorExito.setContent("Registrado correctamente");
					registroVendedorExito.addReceiver(msjRegistro.getSender());
					send(registroVendedorExito);
					System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de registro con exito de vendedor a " + vendedor.getNombre());
				} else {
					ACLMessage registroVendedor = new ACLMessage(ACLMessage.FAILURE);
					registroVendedor.setConversationId("RegistroVendedor");
					registroVendedor.setContent("Fallo en el registro");
					registroVendedor.addReceiver(msjRegistro.getSender());
					send(registroVendedor);
					System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en el registro de vendedor a " + vendedor.getNombre());
				}
				
			}
		}

		@Override
		public boolean done() {
			/*for (Vendedor vendedor : vendedores) {
				System.out.println("Vendedor en lista de vendedores:" + vendedor.getNombre());
			}
			if (vendedores.contains(vendedor)) { // Se ha a�adido el vendedor
				ACLMessage registroVendedorExito = new ACLMessage(ACLMessage.INFORM);
				registroVendedorExito.setConversationId("RegistroVendedor");
				registroVendedorExito.setContent("Registrado correctamente");
				send(registroVendedorExito);
				System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de registro con exito de vendedor a " + vendedor.getNombre());
				return true;
			} else { // No se ha a�adido el vendedor
				ACLMessage registroVendedor = new ACLMessage(ACLMessage.FAILURE);
				registroVendedor.setConversationId("RegistroVendedor");
				registroVendedor.setContent("Fallo en el registro");
				send(registroVendedor);
				//System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en el registro de vendedor a " + vendedor.getNombre());
				return false;
			}
			return false;

		}
	}*/

	public void addVendedor(Vendedor vendedor) {
		if (!this.vendedores.contains(vendedor)) {
			this.vendedores.add(vendedor);
		}
		
	}

	public boolean containsVendedor(Vendedor vendedor) {
		return this.vendedores.contains(vendedor);
	};
}
