package POA.Protocolos;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;

import java.io.IOException;

import POA.Agentes.AgenteComprador;
import POA.Agentes.POAAgent;
import POA.Ontologia.Articulo;

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
			return puja;
		}
		return null;	
	}

}
