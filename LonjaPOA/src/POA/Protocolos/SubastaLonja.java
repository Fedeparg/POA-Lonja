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

	public SubastaLonja(Agent a, ACLMessage cfp, Articulo articuloActual) {
		super(a, cfp);
		this.articuloActual = articuloActual;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void handleAllResponses(Vector respuestas, Vector aceptaciones) {
		((POAAgent) myAgent).getLogger().info("Subasta", "Recibidas " + respuestas.size() + " peticiones de compra");
		boolean articuloAdjudicado = false;
		
		if (respuestas.size() > 0) {

			// Ordenamos las respuestas por fecha de envio
			respuestas.sort(new Comparator<ACLMessage>() {
				@Override
				public int compare(ACLMessage arg0, ACLMessage arg1) {
					if (arg0.getPostTimeStamp() > arg1.getPostTimeStamp())
						return -1;
					else
						return 1;
				}
			});
			
			// Respondemos a todas las propuestad que recibimos
			for (int i = 0; i < respuestas.size(); i++) {

				ACLMessage respuesta = (ACLMessage) respuestas.get(i);
				ACLMessage msjRespuesta = ((ACLMessage) respuestas.get(i)).createReply();
				if (!articuloAdjudicado && ((AgenteLonja) myAgent).suficienteDinero(respuesta.getSender(),
						articuloActual.getPrecio())) {
					// Aceptamos la primera propuesta que nos ha llegado si el comprador tiene suficiente dinero
					msjRespuesta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					((AgenteLonja) myAgent).articuloVendido(articuloActual, respuesta.getSender());
					articuloAdjudicado = true;
					// Evitamos que haya mas rondas de venta de este articulo
					((AgenteLonja) myAgent).setState(0);
					((AgenteLonja) myAgent).setSubastaEnMarcha(false);
					((POAAgent) myAgent).getLogger().info("Subasta", "Vendido el articulo " + articuloActual);
				} else {
					// Rechazamos el resto de propuestas
					msjRespuesta.setPerformative(ACLMessage.REJECT_PROPOSAL);
				}
				aceptaciones.add(msjRespuesta);
			}
		} 
		
		// Si no recibimos respuestas o ningun comprador tiene suficiente dinero
		if (respuestas.size() == 0 || !articuloAdjudicado) {
			if (((AgenteLonja) myAgent).reducirPrecio(articuloActual)) {
				// Si el precio sigue por encima del precio minimo, lo seguimos intentar vender
				((POAAgent) myAgent).getLogger().info("Subasta",
						"Reintentando subasta de articulo" + articuloActual + " con precio " + articuloActual.getPrecio());
			} else {
				// Si no, lo a�adimos a la lista de imposibles y la subasta de ese articulo se termina
				((AgenteLonja) myAgent).imposibleVender(articuloActual);
				((AgenteLonja) myAgent).setPuja(false);
			}
			((AgenteLonja) myAgent).setSubastaEnMarcha(false);

		}
	}
	
	protected void handleInform(ACLMessage inform) {
		// Cuando el comprador nos informa de que ha ha recibo el mensaje aceptando su propuesta 
		// concluimos la subasta del articulo actual
		((AgenteLonja) myAgent).setPuja(false);
	}

}
