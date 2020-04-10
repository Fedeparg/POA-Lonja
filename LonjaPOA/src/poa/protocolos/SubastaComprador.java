package poa.protocolos;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import poa.agentes.AgenteComprador;
import poa.agentes.POAAgent;
import poa.ontologia.Articulo;

import java.io.IOException;

@SuppressWarnings("serial")
public class SubastaComprador extends ContractNetResponder {

	public SubastaComprador(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
		((POAAgent) myAgent).getLogger().info("Subasta", "Recibida oferta de compra");
		String pescadito = null;
		double precio = 0;
		try {
			pescadito = ((Articulo) cfp.getContentObject()).getPescado();
			precio = ((Articulo) cfp.getContentObject()).getPrecio();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		// Comprobar que el pescado interesa
		if (((AgenteComprador) myAgent).interesaPescado(pescadito, precio)) {
			ACLMessage puja = cfp.createReply();
			puja.setPerformative(ACLMessage.PROPOSE);
			try {
				puja.setContentObject(cfp.getContentObject());
			} catch (IOException | UnreadableException e) {
				e.printStackTrace();
			}
			((POAAgent) myAgent).getLogger().info("Subasta", "Pujo por el artículo");
			return puja;
		}
		return null;
	}

	protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
			throws FailureException {
		((POAAgent) myAgent).getLogger().info("Subasta", "He comprado cosas");
		ACLMessage response = accept.createReply();
		response.setPerformative(ACLMessage.INFORM);
		Articulo articulo = null;
		try {
			articulo = (Articulo) cfp.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		((AgenteComprador) myAgent).eliminarListaCompra(articulo);
		return response;
	}

	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
		((POAAgent) myAgent).getLogger().info("Subasta", "Pos se me han adelantado");
	}

}
