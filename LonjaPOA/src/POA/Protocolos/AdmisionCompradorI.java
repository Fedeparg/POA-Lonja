package POA.Protocolos;

import POA.Agentes.POAAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

@SuppressWarnings("serial")
public class AdmisionCompradorI extends AchieveREInitiator {

	public AdmisionCompradorI(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage msjRegistroExisto) {
		((POAAgent) myAgent).getLogger().info("AdmisionComprador", "Recibido mensaje de aceptacion registro en  "
				+ msjRegistroExisto.getSender().getLocalName());
	}

	protected void handleFailure(ACLMessage msjRegistroFallo) {
		((POAAgent) myAgent).getLogger().info("AdmisionComprador", "Recibido mensaje de fallo en registro en  "
				+ msjRegistroFallo.getSender().getLocalName());
		this.reset();
	}

}
