package POA.Protocolos;

import POA.Agentes.AgenteComprador;
import POA.Agentes.POAAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

@SuppressWarnings("serial")
public class AperturaCreditoComprador extends AchieveREInitiator {

	public AperturaCreditoComprador(Agent a, ACLMessage msg) {
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
