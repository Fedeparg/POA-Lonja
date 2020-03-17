package Protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

@SuppressWarnings("serial")
public class AperturaCreditoComprador extends AchieveREInitiator {

	public AperturaCreditoComprador(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage msjRegistroExisto) {
		System.out.println(this.myAgent.getLocalName() + ": Recibido mensaje de aceptacion apertura credito en  "
				+ msjRegistroExisto.getSender().getLocalName());
	}

	protected void handleFailure(ACLMessage msjRegistroFallo) {
		System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en la apertura credito en  "
				+ msjRegistroFallo.getSender().getLocalName());
		this.reset();
	}

}
