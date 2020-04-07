package POA.Protocolos;

import POA.Agentes.AgenteComprador;
import POA.Agentes.POAAgent;
import POA.Ontologia.Articulo;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

@SuppressWarnings("serial")
public class RetiradaArticuloComprador extends AchieveREInitiator {
	Articulo articulo = null;
	
	public RetiradaArticuloComprador(Agent a, ACLMessage msg, Articulo articulo) {
		super(a, msg);
		this.articulo = articulo;
	}

	protected void handleInform(ACLMessage msjRetiradaArticulo) {
		
//		try {
//			articulo = (Articulo) msjRetiradaArticulo.getContentObject();
//		} catch (UnreadableException e) {
//			e.printStackTrace();
//		}
		
		((AgenteComprador) myAgent).retiradaArticulo(articulo);
		((POAAgent) myAgent).getLogger().info("RetiradaArticulo", "Recibido el articulo");
		((AgenteComprador) myAgent).setRetiradaEnMarcha(false);
	}

	protected void handleFailure(ACLMessage RetiradaArticuloFallo) {
		((POAAgent) myAgent).getLogger().info("RetiradaArticulo", "Se ha producido un error");
		this.reset();
	}

}