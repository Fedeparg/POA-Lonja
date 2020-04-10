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
import poa.ontologia.Comprador;

@SuppressWarnings("serial")
public class RegistroCompradorParticipant extends AchieveREResponder {

	public RegistroCompradorParticipant(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
		((POAAgent) myAgent).getLogger().info("AdmisionComprador", "Recibida peticion de registro comprador de "
				+ request.getSender().getLocalName());
		Comprador comprador = null;
		try {
			comprador = (Comprador) request.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el vendedor del mensaje de registro");
			e.printStackTrace();
		}
		// A�adimos el vendedor a lista de vendedores
		ACLMessage msjRespuesta = request.createReply();
		if (comprador != null && !((AgenteLonja) this.myAgent).containsComprador(request.getSender())) {
			((AgenteLonja) this.myAgent).addComprador(request.getSender());
			
			((POAAgent) myAgent).getLogger().info("AdmisionComprador",
					"Anadido comprador " + request.getSender().getLocalName());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			msjRespuesta.setContent("Registrado correctamente");
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en el registro");
		}
		return msjRespuesta;
	}

}
