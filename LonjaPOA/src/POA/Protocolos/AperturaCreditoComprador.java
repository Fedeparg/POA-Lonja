package POA.Protocolos;

import POA.Agentes.AgenteComprador;
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
		System.out.println(this.myAgent.getLocalName() + ": Recibido mensaje de aceptacion apertura credito en  "
				+ msjRegistroExito.getSender().getLocalName());
		try {
			((AgenteComprador) this.myAgent).cambiarDinero((Double) msjRegistroExito.getContentObject());
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	protected void handleFailure(ACLMessage msjRegistroFallo) {
		System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en la apertura credito en  "
				+ msjRegistroFallo.getSender().getLocalName());
		this.reset();
	}

}
