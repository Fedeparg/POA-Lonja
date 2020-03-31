package POA.Protocolos;

import java.util.Comparator;
import java.util.Vector;

import POA.Agentes.AgenteLonja;
import POA.Ontologia.Articulo;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

@SuppressWarnings("serial")
public class SubastaLonja extends ContractNetInitiator {

	private Articulo articuloActual;

	public SubastaLonja(Agent a, ACLMessage cfp, Articulo articuloActual) {
		super(a, cfp);
		this.articuloActual = articuloActual;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void handleAllResponses(Vector respuestas, Vector aceptaciones) {
		if (respuestas.size() > 0) {

			// Ordenamos las respuestas
			respuestas.sort(new Comparator<ACLMessage>() {
				@Override
				public int compare(ACLMessage arg0, ACLMessage arg1) {
					if (arg0.getPostTimeStamp() > arg1.getPostTimeStamp())
						return -1;
					else
						return 1;
				}
			});

			ACLMessage respuesta = (ACLMessage) respuestas.get(0);
			AID comprador = respuesta.getSender();
			Articulo articulo = null;
			try {
				articulo = (Articulo) respuesta.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			int i = 1;
			while (!((AgenteLonja) myAgent).suficienteDinero(comprador, articulo.getPrecio())) {
				comprador = ((ACLMessage) respuestas.get(i)).getSender();
				i = i++;
			}
			ACLMessage msjRespuesta = respuesta.createReply();
			msjRespuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			aceptaciones.add(msjRespuesta);
			for (int j = 0; j < respuestas.size(); j++) {
				if (i != j++) {
					ACLMessage msjRespuestaMala = ((ACLMessage) respuestas.get(j)).createReply();
					msjRespuestaMala.setPerformative(ACLMessage.REJECT_PROPOSAL);
					aceptaciones.add(msjRespuestaMala);
				}
			}

			((AgenteLonja) myAgent).articuloVendido(articulo);
		} else {
			((AgenteLonja) myAgent).reducirPrecio(articuloActual);
		}
	}
	
}
