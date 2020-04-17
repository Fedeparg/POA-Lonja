package poa.protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import poa.agentes.AgenteVendedor;
import poa.agentes.POAAgent;

@SuppressWarnings("serial")
public class DepositoArticuloInitiator extends AchieveREInitiator {

	public DepositoArticuloInitiator(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage inform) {
		((POAAgent) myAgent).getLogger().info("DepositoArticulo",
				"Recibido mensaje de aceptacion de deposito en " + inform.getSender().getLocalName());

		((AgenteVendedor) myAgent).removeSequentialBehaviour(this);
	}

	protected void handleFailure(ACLMessage failure) {
		((POAAgent) myAgent).getLogger().info("DepositoArticulo",
				"Recibido mensaje de fallo en deposito en " + failure.getSender().getLocalName());

		this.reset();
	}

}
