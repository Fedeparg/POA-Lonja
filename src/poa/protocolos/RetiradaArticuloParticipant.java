package poa.protocolos;

import java.io.IOException;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import poa.agentes.POAAgent;
import poa.ontologia.Articulo;

@SuppressWarnings("serial")
public class RetiradaArticuloParticipant extends AchieveREResponder {

	public RetiradaArticuloParticipant(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
		((POAAgent) myAgent).getLogger().info("RetiradaArticulo",
				"Recibida peticion de retirada de " + request.getSender().getLocalName());
		Articulo articulo = null;

		try {
			articulo = (Articulo) request.getContentObject();
		} catch (UnreadableException e) {
			((POAAgent) myAgent).getLogger().info("RetiradaArticulo",
					"Fallo al retirar el articulo de " + request.getSender().getLocalName());
			e.printStackTrace();
		}

		ACLMessage msjRespuesta = request.createReply();
		if (articulo != null) {
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			try {
				msjRespuesta.setContentObject(articulo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo al retirar articulo");
		}

		return msjRespuesta;
	}

}