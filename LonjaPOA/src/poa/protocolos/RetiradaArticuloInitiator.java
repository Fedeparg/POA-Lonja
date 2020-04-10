package poa.protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import poa.agentes.AgenteComprador;
import poa.agentes.POAAgent;
import poa.ontologia.Articulo;

@SuppressWarnings("serial")
public class RetiradaArticuloInitiator extends AchieveREInitiator {
	Articulo articulo = null;
	
	public RetiradaArticuloInitiator(Agent a, ACLMessage msg, Articulo articulo) {
		super(a, msg);
		this.articulo = articulo;
	}

	protected void handleInform(ACLMessage msjRetiradaArticulo) {
		((AgenteComprador) myAgent).retiradaArticulo(articulo);
		((POAAgent) myAgent).getLogger().info("RetiradaArticulo", "Recibido el articulo");
		((AgenteComprador) myAgent).setRetiradaEnMarcha(false);
	}

	protected void handleFailure(ACLMessage RetiradaArticuloFallo) {
		((POAAgent) myAgent).getLogger().info("RetiradaArticulo", "Se ha producido un error");
		this.reset();
	}

}