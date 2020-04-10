package poa.protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import poa.agentes.AgenteVendedor;
import poa.agentes.POAAgent;

@SuppressWarnings("serial")
public class RegistroVendedorInitiator extends AchieveREInitiator {

	public RegistroVendedorInitiator(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage inform) {

		((POAAgent) myAgent).getLogger().info("AdmisionVendedor",
				"Recibido mensaje de aceptacion registro en " + inform.getSender().getLocalName());
		((AgenteVendedor) myAgent).removeSequentialBehaviour(this);
	}

	protected void handleFailure(ACLMessage failure) {
		((POAAgent) myAgent).getLogger().info("AdmisionVendedor",
				"Recibido mensaje de fallo en registro en " + failure.getSender().getLocalName());
	}
	
}
