package poa.protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import poa.agentes.AgenteComprador;
import poa.agentes.POAAgent;

@SuppressWarnings("serial")
public class RegistroCompradorInitiator extends AchieveREInitiator {

	public RegistroCompradorInitiator(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage inform) {
		((POAAgent) myAgent).getLogger().info("AdmisionComprador", "Recibido mensaje de aceptacion registro en  "
				+ inform.getSender().getLocalName());
		((AgenteComprador) myAgent).removeBehaviour(this);
	}

	protected void handleFailure(ACLMessage failure) {
		((POAAgent) myAgent).getLogger().info("AdmisionComprador", "Recibido mensaje de fallo en registro en  "
				+ failure.getSender().getLocalName());
		this.reset();
	}

}
