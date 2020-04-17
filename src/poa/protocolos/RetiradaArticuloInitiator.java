package poa.protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
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

	protected void handleInform(ACLMessage inform) {
		((AgenteComprador) myAgent).retiradaArticulo(articulo);

		((POAAgent) myAgent).getLogger().info("RetiradaArticulo", "Recibido el articulo " + articulo);

		((AgenteComprador) myAgent).setRetiradaEnMarcha(false);
	}

	protected void handleFailure(ACLMessage failure) {
		((POAAgent) myAgent).getLogger().info("RetiradaArticulo", "Se ha producido un error al retirar el articulo");

		this.reset();
	}

}