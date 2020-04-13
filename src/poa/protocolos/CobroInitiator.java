package poa.protocolos;

import java.util.LinkedList;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;
import poa.agentes.AgenteLonja;
import poa.agentes.POAAgent;
import poa.ontologia.Articulo;

@SuppressWarnings("serial")
public class CobroInitiator extends ProposeInitiator {

	private LinkedList<Articulo> articulos;

	public CobroInitiator(Agent a, ACLMessage msg, LinkedList<Articulo> articulos) {
		super(a, msg);
		this.articulos = articulos;
	}

	protected void handleAcceptProposal(ACLMessage accept_proposal) {
		((POAAgent) myAgent).getLogger().info("Cobro", "Eliminando articulos cobrados...");
		((AgenteLonja) myAgent).eliminarArticulosCobrados(accept_proposal.getSender(), articulos);
		((AgenteLonja) myAgent).setCobroEnMarcha(false);
		((AgenteLonja) myAgent).setEstadoCobro(0);
	}
	
	protected void handleRejectProposal(ACLMessage reject_proposal) {
		((POAAgent) myAgent).getLogger().info("Cobro", "El vendedor ha rechazado el pago...");
		((AgenteLonja) myAgent).setCobroEnMarcha(false);
		((AgenteLonja) myAgent).setEstadoCobro(0);
	}

}
