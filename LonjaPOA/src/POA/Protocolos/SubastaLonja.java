package POA.Protocolos;

import java.util.Comparator;
import java.util.Vector;

import POA.Agentes.AgenteLonja;
import POA.Agentes.POAAgent;
import POA.Ontologia.Articulo;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

@SuppressWarnings("serial")
public class SubastaLonja extends ContractNetInitiator {

	private Articulo articuloActual;
	private ACLMessage cfp;

	public SubastaLonja(Agent a, ACLMessage cfp, Articulo articuloActual) {
		super(a, cfp);
		this.cfp = cfp;
		this.articuloActual = articuloActual;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void handleAllResponses(Vector respuestas, Vector aceptaciones) {
		((POAAgent) myAgent).getLogger().info("Subasta", "Recibidas " + respuestas.size() + " peticiones de compra");
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
			boolean articuloAdjudicado = false;
			for (int i = 0; i < respuestas.size(); i++) {

				ACLMessage respuesta = (ACLMessage) respuestas.get(i);
				ACLMessage msjRespuesta = ((ACLMessage) respuestas.get(i)).createReply();
				if (!articuloAdjudicado && ((AgenteLonja) myAgent).suficienteDinero(respuesta.getSender(),
						articuloActual.getPrecio())) {
					msjRespuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					((AgenteLonja) myAgent).articuloVendido(articuloActual, respuesta.getSender());
					articuloAdjudicado = true;
					((AgenteLonja) myAgent).setSubastaEnMarcha(false);
					((POAAgent) myAgent).getLogger().info("Subasta", "Vendido el articulo " + articuloActual);
				} else {
					msjRespuesta.setPerformative(ACLMessage.REJECT_PROPOSAL);
				}
				aceptaciones.add(msjRespuesta);
			}
			if (!articuloAdjudicado) {
				if (((AgenteLonja) myAgent).reducirPrecio(articuloActual)) {
					((POAAgent) myAgent).getLogger().info("Subasta", "Reintentando subasta de articulo" + articuloActual
							+ " con precio " + articuloActual.getPrecio());
					((AgenteLonja) myAgent).setSubastaEnMarcha(false);
				} else {
					((AgenteLonja) myAgent).imposibleVender(articuloActual);
				}

			}
		} else {
			if (((AgenteLonja) myAgent).reducirPrecio(articuloActual)) {
				((POAAgent) myAgent).getLogger().info("Subasta",
						"Reintentando subasta de articulo" + articuloActual + " con precio " + articuloActual.getPrecio());
				((AgenteLonja) myAgent).setSubastaEnMarcha(false);
			} else {
				((AgenteLonja) myAgent).imposibleVender(articuloActual);
			}

		}
	}
	

}
