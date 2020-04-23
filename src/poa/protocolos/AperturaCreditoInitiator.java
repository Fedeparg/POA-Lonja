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

	protected void handleInform(ACLMessage inform) {
		((POAAgent) myAgent).getLogger().info("AperturaCredito",
				"Recibido mensaje de aceptacion apertura credito en " + inform.getSender().getLocalName());

		Double dinero = 0.0;
		try {
			dinero = (Double) inform.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		((AgenteComprador) myAgent).restarDineroComprador(dinero);

		((AgenteComprador) myAgent).removeSequentialBehaviour(this);
	}

	protected void handleFailure(ACLMessage failure) {
		((POAAgent) myAgent).getLogger().info("AperturaCredito",
				"Recibido mensaje de fallo en apertura credito en " + failure.getSender().getLocalName());

		this.reset();
	}

}
