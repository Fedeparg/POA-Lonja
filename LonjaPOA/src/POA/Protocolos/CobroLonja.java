package POA.Protocolos;

import java.util.LinkedList;

import POA.Ontologia.Articulo;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;
import POA.Agentes.AgenteLonja;
import POA.Agentes.POAAgent;

@SuppressWarnings("serial")
public class CobroLonja extends ProposeInitiator {

	private LinkedList<Articulo> articulos;

	public CobroLonja(Agent a, ACLMessage msg, LinkedList<Articulo> articulos) {
		super(a, msg);
		this.articulos = articulos;
	}

	protected void handleAcceptProposal(ACLMessage accept_proposal) {
		((POAAgent) myAgent).getLogger().info("Cobro", "Eliminando articulos cobrados...");
		((AgenteLonja) myAgent).eliminarArticulosCobrados(accept_proposal.getSender(), articulos);
		((AgenteLonja) myAgent).setCobroEnMarcha(false);
		((AgenteLonja) myAgent).setEstadoCobro(0);
	}

}
