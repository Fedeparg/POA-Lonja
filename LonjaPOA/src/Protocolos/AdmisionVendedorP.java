package Protocolos;

import Agentes.AgenteLonja;
import Ontologia.Vendedor;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

public class AdmisionVendedorP extends AchieveREResponder{

	public AdmisionVendedorP(Agent a, MessageTemplate mt) {
		super(a, mt);
	}
	
	protected ACLMessage prepareResponse(ACLMessage msjRegistro) throws NotUnderstoodException, RefuseException {
		System.out.println(this.getAgent().getLocalName() + ": Recibida peticiï¿½n de registro vendedor de " + msjRegistro.getSender().getLocalName());
		Vendedor vendedor = null;
		try {
			vendedor = (Vendedor) msjRegistro.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el vendedor del mensaje de registro");
			e.printStackTrace();
		}
		// Añadimos el vendedor a lista de vendedores
		if (vendedor != null && !((AgenteLonja) this.myAgent).containsVendedor(vendedor)) {
			System.out.println(this.getAgent().getLocalName() + ": Añadido vendedor " + msjRegistro.getSender().getLocalName());
			((AgenteLonja) this.myAgent).addVendedor(vendedor);
			ACLMessage registroVendedorExito = new ACLMessage(ACLMessage.INFORM);
			registroVendedorExito.setConversationId("RegistroVendedor");
			registroVendedorExito.setContent("Registrado correctamente");
			registroVendedorExito.addReceiver(msjRegistro.getSender());
			System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de registro con exito de vendedor a " + vendedor.getNombre());
			return registroVendedorExito;
		} else {
			ACLMessage registroVendedorFallo = new ACLMessage(ACLMessage.FAILURE);
			registroVendedorFallo.setConversationId("RegistroVendedor");
			registroVendedorFallo.setContent("Fallo en el registro");
			registroVendedorFallo.addReceiver(msjRegistro.getSender());
			System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en el registro de vendedor a " + vendedor.getNombre());
			return registroVendedorFallo;
		}
	}


}
