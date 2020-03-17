package Protocolos;

import Agentes.AgenteLonja;
import Ontologia.Articulo;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

@SuppressWarnings("serial")
public class DepositoArticuloP extends AchieveREResponder {

	public DepositoArticuloP(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage prepareResponse(ACLMessage msjDeposito) {
		System.out.println(myAgent.getLocalName() + ": recibido mensaje de deposito de articulo");
		Articulo articulo = null;
		try {
			articulo = (Articulo) msjDeposito.getContentObject();
		} catch (UnreadableException e) {
			System.out.println(myAgent.getLocalName() + ": fallo al extraer el articulo del mensaje");
			e.printStackTrace();
		}
		ACLMessage msjRespuesta = msjDeposito.createReply();
		if (articulo != null && ((AgenteLonja) myAgent).containsVendedor(msjDeposito.getSender())) {
			((AgenteLonja) myAgent).addArticuloParaSubastar(articulo);
			System.out.println(myAgent.getLocalName() + ": Depositado articulo " + articulo.getID());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			msjRespuesta.setContent("Articulo depositado con exito");
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo al depositar el articulo");
		}
		return msjRespuesta;

	}

}
