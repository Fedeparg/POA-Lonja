package poa.protocolos;

import java.io.IOException;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import poa.agentes.AgenteLonja;
import poa.agentes.POAAgent;

@SuppressWarnings("serial")
public class AperturaCreditoParticipant extends AchieveREResponder {

	public AperturaCreditoParticipant(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
		((POAAgent) myAgent).getLogger().info("AperturaCredito",
				"Recibida peticion de apertura credito de " + request.getSender().getLocalName());
		Double dinero = 0.0;
		try {
			dinero = (Double) request.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el dinero del mensaje de registro");
			e.printStackTrace();
		}
		// A�adimos el vendedor a lista de vendedores
		ACLMessage msjRespuesta = request.createReply();
		if (dinero != null && ((AgenteLonja) this.myAgent).containsComprador(request.getSender())) {
			((AgenteLonja) this.myAgent).addDineroComprador(request.getSender(), dinero);
			((POAAgent) myAgent).getLogger().info("AperturaCredito",
					"Añadido " + dinero + "al comprador " + request.getSender().getLocalName());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			try {
				msjRespuesta.setContentObject(dinero);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en apertura credito");
			System.out.println(
					this.myAgent.getLocalName() + ": Enviando mensaje de fallo en apertura credito de comprador a "
							+ request.getSender().getLocalName());
		}
		return msjRespuesta;
	}

}
