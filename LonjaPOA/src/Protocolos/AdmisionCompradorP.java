package Protocolos;

import Agentes.AgenteLonja;
import Ontologia.Comprador;
import jade.core.Agent;
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
		// Añadimos el vendedor a lista de vendedores
		ACLMessage msjRespuesta = msjRegistro.createReply();
		if (comprador != null && !((AgenteLonja) this.myAgent).containsComprador(msjRegistro.getSender())) {
			((AgenteLonja) this.myAgent).addComprador(msjRegistro.getSender(), comprador);
			System.out.println( this.getAgent().getLocalName() + ": Anadido comprador " + msjRegistro.getSender().getLocalName());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			msjRespuesta.setContent("Registrado correctamente");
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en el registro");
			System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en el registro de comprador a "
					+ comprador.getNombre());
		}
		return msjRespuesta;
	}

}
