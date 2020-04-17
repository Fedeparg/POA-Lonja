package poa.protocolos;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import poa.agentes.AgenteLonja;
import poa.agentes.POAAgent;
import poa.ontologia.Vendedor;

@SuppressWarnings("serial")
public class RegistroVendedorParticipant extends AchieveREResponder {

	public RegistroVendedorParticipant(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
		((POAAgent) myAgent).getLogger().info("AdmisionVendedor",
				"Recibida peticion de registro vendedor de " + request.getSender().getLocalName());
		Vendedor vendedor = null;
		try {
			vendedor = (Vendedor) request.getContentObject();
		} catch (UnreadableException e) {
			((POAAgent) myAgent).getLogger().info("AdmisionVendedor",
					"Fallo al reigstrar el vendedor " + request.getSender().getLocalName());
			e.printStackTrace();
		}
		// A�adimos el vendedor a lista de vendedores y enviamos la respuesta
		ACLMessage msjRespuesta = request.createReply();
		if (vendedor != null && !((AgenteLonja) this.myAgent).containsVendedor(request.getSender())) {
			((AgenteLonja) this.myAgent).addVendedor(request.getSender());
			((POAAgent) myAgent).getLogger().info("AdmisionVendedor",
					"Añadido vendedor " + request.getSender().getLocalName());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			msjRespuesta.setContent("Registrado correctamente");
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en el registro");
		}
		return msjRespuesta;
	}

}
