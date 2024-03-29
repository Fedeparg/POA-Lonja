package poa.protocolos;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import poa.agentes.AgenteLonja;
import poa.agentes.POAAgent;
import poa.ontologia.Articulo;

@SuppressWarnings("serial")
public class DepositoArticuloParticipant extends AchieveREResponder {

	public DepositoArticuloParticipant(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleRequest(ACLMessage request) {
		((POAAgent) myAgent).getLogger().info("DepositoArticulo",
				"Procesando peticion de deposito de " + request.getSender().getLocalName());
		Articulo articulo = null;
		try {
			articulo = (Articulo) request.getContentObject();
		} catch (UnreadableException e) {
			((POAAgent) myAgent).getLogger().info("DepositoArticulo",
					"Fallo al depositar " + articulo + " del " + request.getSender().getLocalName());
			e.printStackTrace();
		}
		ACLMessage msjRespuesta = request.createReply();
		if (articulo != null && ((AgenteLonja) myAgent).containsVendedor(request.getSender())) {
			((AgenteLonja) myAgent).addArticuloParaSubastar(articulo, request.getSender());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			msjRespuesta.setContent("Articulo depositado con exito");

			((POAAgent) myAgent).getLogger().info("DepositoArticulo",
					"Depositados " + articulo + " del " + request.getSender().getLocalName() + " con exito");
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo al depositar el articulo");
		}
		return msjRespuesta;

	}

}
