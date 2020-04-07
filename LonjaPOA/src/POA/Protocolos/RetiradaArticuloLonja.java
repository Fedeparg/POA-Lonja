package POA.Protocolos;

import java.io.IOException;

import POA.Agentes.AgenteLonja;
import POA.Agentes.POAAgent;
import POA.Ontologia.Articulo;
import POA.Ontologia.Vendedor;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

@SuppressWarnings("serial")
public class RetiradaArticuloLonja extends AchieveREResponder {

	public RetiradaArticuloLonja(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleRequest(ACLMessage msjRetiradaArticulo) throws NotUnderstoodException, RefuseException {
		((POAAgent) myAgent).getLogger().info("RetiradaArticulo", "Recibida peticion de retirada de "
				+ msjRetiradaArticulo.getSender().getLocalName());
		Articulo articulo= null;
		try {
			articulo = (Articulo) msjRetiradaArticulo.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el articulo del mensaje de registro");
			e.printStackTrace();
		}
		// A�adimos el vendedor a lista de vendedores y enviamos la respuesta
		ACLMessage msjRespuesta = msjRetiradaArticulo.createReply();
		if (articulo != null) {	
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			try {
				msjRespuesta.setContentObject(articulo);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo al retirar articulo");
		}
		return msjRespuesta;
	}

}