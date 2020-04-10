package poa.protocolos;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeResponder;
import poa.agentes.AgenteVendedor;
import poa.agentes.POAAgent;

@SuppressWarnings("serial")
public class CobroParticipant extends ProposeResponder {

	public CobroParticipant(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage prepareResponse(ACLMessage propose) throws NotUnderstoodException, RefuseException {
		ACLMessage reply = propose.createReply();
		Double dinero = Double.parseDouble(propose.getContent());
		((AgenteVendedor) myAgent).addGanancias(dinero);
		reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
		((POAAgent) myAgent).getLogger().info("Cobro", "He recibido el dinero" + dinero);
		return reply;
	}

}
