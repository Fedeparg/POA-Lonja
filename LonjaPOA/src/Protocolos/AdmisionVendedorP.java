package Protocolos;

import Agentes.AgenteLonja;
import Ontologia.Vendedor;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

@SuppressWarnings("serial")
public class AdmisionVendedorP extends AchieveREResponder {

	public AdmisionVendedorP(Agent a, MessageTemplate mt) {
		super(a, mt);
	}

	protected ACLMessage prepareResponse(ACLMessage msjRegistro) throws NotUnderstoodException, RefuseException {
		System.out.println(this.getAgent().getLocalName() + ": Recibida peticion de registro vendedor de "
				+ msjRegistro.getSender().getLocalName());
		Vendedor vendedor = null;
		try {
			vendedor = (Vendedor) msjRegistro.getContentObject();
		} catch (UnreadableException e) {
			System.out.println("Fallo al sacar el vendedor del mensaje de registro");
			e.printStackTrace();
		}
		// Añadimos el vendedor a lista de vendedores y enviamos la respuesta
		ACLMessage msjRespuesta = msjRegistro.createReply();
		if (vendedor != null && !((AgenteLonja) this.myAgent).containsVendedor(msjRegistro.getSender())) {
			((AgenteLonja) this.myAgent).addVendedor(msjRegistro.getSender(), vendedor);
			System.out.println(
					this.getAgent().getLocalName() + ": Anadido vendedor " + msjRegistro.getSender().getLocalName());
			msjRespuesta.setPerformative(ACLMessage.INFORM);
			msjRespuesta.setContent("Registrado correctamente");
		} else {
			msjRespuesta.setPerformative(ACLMessage.FAILURE);
			msjRespuesta.setContent("Fallo en el registro");
			System.out.println(this.myAgent.getLocalName() + ": Enviando mensaje de fallo en el registro de vendedor a "
					+ vendedor.getNombre());
		}
		return msjRespuesta;
	}

}
