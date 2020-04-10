package POA.Protocolos;

import POA.Agentes.AgenteVendedor;
import POA.Agentes.POAAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

@SuppressWarnings("serial")
public class AdmisionVendedorI extends AchieveREInitiator {

	public AdmisionVendedorI(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage msjRegistroExito) {

		((POAAgent) myAgent).getLogger().info("AdmisionVendedor",
				"Recibido mensaje de aceptacion registro en " + msjRegistroExito.getSender().getLocalName());
		((AgenteVendedor) myAgent).removeSequentialBehaviour(this);
	}

	protected void handleFailure(ACLMessage msjRegistroFallo) {
		((POAAgent) myAgent).getLogger().info("AdmisionVendedor",
				"Recibido mensaje de fallo en registro en " + msjRegistroFallo.getSender().getLocalName());
	}
	
}
