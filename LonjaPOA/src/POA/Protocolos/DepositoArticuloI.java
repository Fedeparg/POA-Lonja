package POA.Protocolos;

import POA.Agentes.POAAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

@SuppressWarnings("serial")
public class DepositoArticuloI extends AchieveREInitiator {

	public DepositoArticuloI(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage msjRegistroExito) {
		((POAAgent) myAgent).getLogger().info("DepositoArticulo",
				"Recibido mensaje de aceptacion de deposito en " + msjRegistroExito.getSender().getLocalName());
	}

	protected void handleFailure(ACLMessage msjRegistroFallo) {
		((POAAgent) myAgent).getLogger().info("DepositoArticulo",
				"Recibido mensaje de fallo en deposito en " + msjRegistroFallo.getSender().getLocalName());
		this.reset();
	}

}
