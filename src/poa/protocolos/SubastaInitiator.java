package poa.protocolos;

import java.util.Vector;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import poa.agentes.AgenteLonja;
import poa.agentes.POAAgent;
import poa.ontologia.Articulo;

@SuppressWarnings("serial")
public class SubastaInitiator extends ContractNetInitiator {

	private Articulo articuloActual;

	public SubastaInitiator(Agent a, ACLMessage cfp, Articulo articuloActual) {
		super(a, cfp);
		this.articuloActual = articuloActual;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void handleAllResponses(Vector responses, Vector acceptances) {
		((POAAgent) myAgent).getLogger().info("Subasta", "Recibidas " + responses.size() + " peticiones de compra");

		boolean articuloAdjudicado = false;

		if (responses.size() > 0) {

			// Ordenamos las respuestas por fecha de envio
			responses.sort((msg0, msg1) -> Long.compare(((ACLMessage)msg0).getPostTimeStamp(),((ACLMessage)msg1).getPostTimeStamp()));

			// Respondemos a todas las propuestad que recibimos
			for (int i = 0; i < responses.size(); i++) {

				ACLMessage respuesta = (ACLMessage) responses.get(i);
				ACLMessage msjRespuesta = ((ACLMessage) responses.get(i)).createReply();
				if (!articuloAdjudicado && ((AgenteLonja) myAgent).suficienteDinero(respuesta.getSender(),
						articuloActual.getPrecio())) {
					// Aceptamos la primera propuesta que nos ha llegado si el comprador tiene
					// suficiente dinero
					msjRespuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					((AgenteLonja) myAgent).articuloVendido(articuloActual, respuesta.getSender());
					articuloAdjudicado = true;

					((POAAgent) myAgent).getLogger().info("Subasta", "Vendido el articulo " + articuloActual
							+ " al comprador " + respuesta.getSender().getLocalName());
				} else {
					// Rechazamos el resto de propuestas
					msjRespuesta.setPerformative(ACLMessage.REJECT_PROPOSAL);
				}
				acceptances.add(msjRespuesta);
			}
		}

		// Si no recibimos respuestas o ningun comprador tiene suficiente dinero
		if (responses.size() == 0 || !articuloAdjudicado) {
			if (((AgenteLonja) myAgent).reducirPrecio(articuloActual)) {
				// Si el precio sigue por encima del precio minimo, lo seguimos intentar vender
				((POAAgent) myAgent).getLogger().info("Subasta", "Reintentando subasta de articulo " + articuloActual
						+ " con precio " + articuloActual.getPrecio());
			} else {
				// Si no, lo agregamos a la lista de imposibles y la subasta de ese articulo se
				// termina
				((AgenteLonja) myAgent).imposibleVender(articuloActual);
				((AgenteLonja) myAgent).setEstadoSubasta(0);
			}
			((AgenteLonja) myAgent).setSubastaEnMarcha(false);

		}
	}

	protected void handleInform(ACLMessage inform) {
		// Cuando el comprador nos informa de que ha ha recibo el mensaje aceptando su
		// propuesta concluimos la subasta del articulo actual
		((AgenteLonja) myAgent).setEstadoSubasta(0);
		((AgenteLonja) myAgent).setSubastaEnMarcha(false);
	}

}
