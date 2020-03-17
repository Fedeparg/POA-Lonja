package Protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

@SuppressWarnings("serial")
public class AdmisionVendedorI extends AchieveREInitiator {

	public AdmisionVendedorI(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage msjRegistroExisto) {
		System.out.println(this.myAgent.getLocalName() + ": Recibido mensaje de aceptacion registro en  "
				+ msjRegistroExisto.getSender().getLocalName());
	}

	protected void handleFailure(ACLMessage msjRegistroFallo) {
		System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en el registro en  "
				+ msjRegistroFallo.getSender().getLocalName());
	}

}
