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
		Double saldo = 0.0;
		try {
			saldo = (Double) request.getContentObject();
			if (saldo == null) {
				throw new IllegalArgumentException("El dinero enviado en el mensaje era null");
			}
		} catch (UnreadableException e) {
			((POAAgent) myAgent).getLogger().info("AperturaCredito",
					"Fallo en la apertura de credito del comprador " + request.getSender().getLocalName());
			e.printStackTrace();
		}
		// Agregamos el vendedor a lista de vendedores
		ACLMessage msjRespuesta = request.createReply();
		if (saldo > 0 && ((AgenteLonja) this.myAgent).containsComprador(request.getSender())) {
			((AgenteLonja) this.myAgent).addDineroComprador(request.getSender(), saldo);

			((POAAgent) myAgent).getLogger().info("AperturaCredito",
					"Añadido " + saldo + " al comprador " + request.getSender().getLocalName());

			msjRespuesta.setPerformative(ACLMessage.INFORM);
			try {
				msjRespuesta.setContentObject(saldo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en apertura credito");
		}
		return msjRespuesta;
	}

}
