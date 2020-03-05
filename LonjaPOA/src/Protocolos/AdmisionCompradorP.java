package Protocolos;

import Agentes.AgenteLonja;
import Ontologia.Comprador;
import Ontologia.Vendedor;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

@SuppressWarnings("serial")
public class AdmisionCompradorP extends AchieveREResponder {

	public AdmisionCompradorP(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage prepareResponse(ACLMessage msjRegistro) throws NotUnderstoodException, RefuseException {
		System.out.println(this.getAgent().getLocalName() + ": Recibida peticion de registro comprador de "
				+ msjRegistro.getSender().getLocalName());
		Comprador comprador = null;
		try {
			comprador = (Comprador) msjRegistro.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el vendedor del mensaje de registro");
			e.printStackTrace();
		}
		// A�adimos el vendedor a lista de vendedores
		if (comprador != null && !((AgenteLonja) this.myAgent).containsComprador(comprador)) {
			System.out.println(
					this.getAgent().getLocalName() + ": Anadido comprador " + msjRegistro.getSender().getLocalName());
			((AgenteLonja) this.myAgent).addComprador(comprador);
			ACLMessage registroVendedorExito = new ACLMessage(ACLMessage.INFORM);
			registroVendedorExito.setConversationId("RegistroComprador");
			registroVendedorExito.setContent("Registrado correctamente");
			registroVendedorExito.addReceiver(msjRegistro.getSender());
			System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de registro con exito de comprador a "
					+ comprador.getNombre());
			return registroVendedorExito;
		} else {
			ACLMessage registroVendedorFallo = new ACLMessage(ACLMessage.FAILURE);
			registroVendedorFallo.setConversationId("RegistroComprador");
			registroVendedorFallo.setContent("Fallo en el registro");
			registroVendedorFallo.addReceiver(msjRegistro.getSender());
			System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en el registro de comprador a "
					+ comprador.getNombre());
			return registroVendedorFallo;
		}
	}

}
