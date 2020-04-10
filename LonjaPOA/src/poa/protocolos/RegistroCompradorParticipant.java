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

	protected ACLMessage handleRequest(ACLMessage msjRegistro) throws NotUnderstoodException, RefuseException {
		((POAAgent) myAgent).getLogger().info("AdmisionComprador", "Recibida peticion de registro comprador de "
				+ msjRegistro.getSender().getLocalName());
		Comprador comprador = null;
		try {
			comprador = (Comprador) msjRegistro.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el vendedor del mensaje de registro");
			e.printStackTrace();
		}
		// A�adimos el vendedor a lista de vendedores
		ACLMessage msjRespuesta = msjRegistro.createReply();
		if (comprador != null && !((AgenteLonja) this.myAgent).containsComprador(msjRegistro.getSender())) {
			((AgenteLonja) this.myAgent).addComprador(msjRegistro.getSender());
			
			((POAAgent) myAgent).getLogger().info("AdmisionComprador",
					"Anadido comprador " + msjRegistro.getSender().getLocalName());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			msjRespuesta.setContent("Registrado correctamente");
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en el registro");
		}
		return msjRespuesta;
	}

}
