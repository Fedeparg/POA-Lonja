package Protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

@SuppressWarnings("serial")
public class DepositoArticuloI extends AchieveREInitiator {

	public DepositoArticuloI(Agent a, ACLMessage msg) {
		super(a, msg);
	}

	protected void handleInform(ACLMessage msjRegistroExisto) {
		System.out.println(this.myAgent.getLocalName() + ": Recibido mensaje de exito en deposito articulo en "
				+ msjRegistroExisto.getSender().getLocalName());
	}

	protected void handleFailure(ACLMessage msjRegistroFallo) {
		System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en deposito de articulo en  "
				+ msjRegistroFallo.getSender().getLocalName());
		this.reset();
	}

}
