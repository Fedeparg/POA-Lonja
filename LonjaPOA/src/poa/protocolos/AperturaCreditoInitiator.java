package poa.protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import poa.agentes.AgenteComprador;
import poa.agentes.POAAgent;

@SuppressWarnings("serial")
public class AperturaCreditoInitiator extends AchieveREInitiator {

	public AperturaCreditoInitiator(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage msjRegistroExito) {
		((POAAgent) myAgent).getLogger().info("AperturaCredito",
				"Recibido mensaje de aceptacion apertura credito en " + msjRegistroExito.getSender().getLocalName());
		((AgenteComprador) myAgent).removeSequentialBehaviour(this);
	}

	protected void handleFailure(ACLMessage msjRegistroFallo) {
		((POAAgent) myAgent).getLogger().info("AperturaCredito",
				"Recibido mensaje de fallo en apertura credito en " + msjRegistroFallo.getSender().getLocalName());
		this.reset();
	}

}
