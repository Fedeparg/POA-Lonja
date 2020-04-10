package poa.protocolos;

import java.io.IOException;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import poa.agentes.AgenteLonja;
import poa.agentes.POAAgent;

@SuppressWarnings("serial")
public class AperturaCreditoLonja extends AchieveREResponder {

	public AperturaCreditoLonja(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleRequest(ACLMessage msjRegistro) throws NotUnderstoodException, RefuseException {
		((POAAgent) myAgent).getLogger().info("AperturaCredito",
				"Recibida peticion de apertura credito de " + msjRegistro.getSender().getLocalName());
		Double dinero = 0.0;
		try {
			dinero = (Double) msjRegistro.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el dinero del mensaje de registro");
			e.printStackTrace();
		}
		// A�adimos el vendedor a lista de vendedores
		ACLMessage msjRespuesta = msjRegistro.createReply();
		if (dinero != null && ((AgenteLonja) this.myAgent).containsComprador(msjRegistro.getSender())) {
			((AgenteLonja) this.myAgent).addDineroComprador(msjRegistro.getSender(), dinero);
			((POAAgent) myAgent).getLogger().info("AperturaCredito",
					"Añadido " + dinero + "al comprador " + msjRegistro.getSender().getLocalName());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			try {
				msjRespuesta.setContentObject(dinero);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en apertura credito");
			System.out.println(
					this.myAgent.getLocalName() + ": Enviando mensaje de fallo en apertura credito de comprador a "
							+ msjRegistro.getSender().getLocalName());
		}
		return msjRespuesta;
	}

}
