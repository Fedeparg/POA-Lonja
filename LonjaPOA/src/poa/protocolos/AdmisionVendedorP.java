package poa.protocolos;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import poa.agentes.AgenteLonja;
import poa.agentes.POAAgent;
import poa.ontologia.Vendedor;

@SuppressWarnings("serial")
public class AdmisionVendedorP extends AchieveREResponder {

	public AdmisionVendedorP(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage handleRequest(ACLMessage msjRegistro) throws NotUnderstoodException, RefuseException {
		((POAAgent) myAgent).getLogger().info("AdmisionVendedor", "Recibida peticion de registro vendedor de "
				+ msjRegistro.getSender().getLocalName());
		Vendedor vendedor = null;
		try {
			vendedor = (Vendedor) msjRegistro.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el vendedor del mensaje de registro");
			e.printStackTrace();
		}
		// A�adimos el vendedor a lista de vendedores y enviamos la respuesta
		ACLMessage msjRespuesta = msjRegistro.createReply();
		if (vendedor != null && !((AgenteLonja) this.myAgent).containsVendedor(msjRegistro.getSender())) {
			((AgenteLonja) this.myAgent).addVendedor(msjRegistro.getSender());
			((POAAgent) myAgent).getLogger().info("AdmisionVendedor", "Añadido vendedor "
					+ msjRegistro.getSender().getLocalName());	
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			msjRespuesta.setContent("Registrado correctamente");
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en el registro");
		}
		return msjRespuesta;
	}

}