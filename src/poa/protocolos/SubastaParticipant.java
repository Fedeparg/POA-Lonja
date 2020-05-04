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
public class SubastaParticipant extends ContractNetResponder {

	public SubastaParticipant(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
		String pescadito = null;
		double precio = 0;
		Articulo articulo = null;
		try {
			articulo = (Articulo) cfp.getContentObject();
			pescadito = articulo.getPescado();
			precio = articulo.getPrecio();
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
			((POAAgent) myAgent).getLogger().info("Subasta", "Pujo por el artículo " + articulo);
			return puja;
		} else {
			((POAAgent)myAgent).getLogger().info("Subasta", "No me interesa el artículo");
			return null;
		}
	}

	protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
			throws FailureException {
		ACLMessage response = accept.createReply();
		response.setPerformative(ACLMessage.INFORM);
		Articulo articulo = null;
		try {
			articulo = (Articulo) cfp.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
		((POAAgent) myAgent).getLogger().info("Subasta", "He comprado el articulo " + articulo);

		((AgenteComprador) myAgent).eliminarListaCompra(articulo);
		return response;
	}

	protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
		((POAAgent) myAgent).getLogger().info("Subasta", "Se me han adelantado");
	}

}
