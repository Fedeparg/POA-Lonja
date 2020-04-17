package poa.agentes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.yaml.snakeyaml.Yaml;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import poa.ontologia.Articulo;
import poa.ontologia.ArticuloCompra;
import poa.ontologia.Comprador;
import poa.protocolos.RegistroCompradorInitiator;
import poa.protocolos.AperturaCreditoInitiator;
import poa.protocolos.RetiradaArticuloInitiator;
import poa.protocolos.SubastaParticipant;

/**
 * La representación del comprador en Jade. Arranca sus comportamientos y
 * realiza las interacciones con los demás agentes.
 *
 */
@SuppressWarnings("serial")
public class AgenteComprador extends POAAgent {

	private Comprador config;
	private AID lonja;
	private boolean retiradaEnMarcha = false;
	SequentialBehaviour seq = new SequentialBehaviour(this);

	public void setup() {

		super.setup();

		Object[] args = getArguments();
		if (args != null && args.length == 1) {
			String configFile = (String) args[0];
			config = initAgentFromConfigFile(configFile);

			if (config != null) {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("lonja");
				sd.setType("lonja");
				template.addServices(sd);
				DFAgentDescription[] result;
				AID[] lonjas = null;
				do {
					try {
						result = DFService.search(this, template);
						lonjas = new AID[result.length];
						for (int i = 0; i < result.length; i++) {
							lonjas[i] = result[i].getName();
						}
					} catch (FIPAException e) {
						e.printStackTrace();
					}
				} while (lonjas.length == 0);
				lonja = lonjas[0];

				// PROTOCOLO REGISTRO COMPRADOR
				seq.addSubBehaviour(protocoloRegistroComprador());

				// PROTOCOLO APERTURA CREDITO
				seq.addSubBehaviour(protocoloAperturaCredito());

				addBehaviour(seq);

				// PROTOCOLO SUBASTA

				MessageTemplate msjPuja = MessageTemplate.MatchConversationId("Subasta");
				addBehaviour(new SubastaParticipant(this, msjPuja));

				// PROTOCOLO RETIRADA DE ARTICULOS
				protocoloRetiradaArticulos();

			} else {
				doDelete();
			}
		} else {
			getLogger().info("ERROR", "Requiere fichero de cofiguración.");
			doDelete();
		}

	}

	/**
	 * Crea el mensaje e inicia el protocolo RegistroComprador
	 */
	private Behaviour protocoloRegistroComprador() {
		ACLMessage msgRegistro = new ACLMessage(ACLMessage.REQUEST);
		msgRegistro.addReceiver(lonja);
		try {
			msgRegistro.setContentObject(config);
		} catch (IOException e) {
			getLogger().info("AdmisionComprador", "Fallo al generar la peticion de registro");
			e.printStackTrace();
		}
		msgRegistro.setConversationId("RegistroComprador");
		msgRegistro.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		getLogger().info("AdmisionComprador", "Enviando peticion de registro");

		return new RegistroCompradorInitiator(this, msgRegistro);
	}

	/**
	 * Crea el mensaje e inicia el protocolo AperturaCredito
	 */
	private Behaviour protocoloAperturaCredito() {
		ACLMessage mensajeAperturaCredito = new ACLMessage(ACLMessage.REQUEST);
		mensajeAperturaCredito.addReceiver(lonja);
		try {
			mensajeAperturaCredito.setContentObject(config.getDinero());
		} catch (IOException e) {
			getLogger().info("AperturaCredito", "Fallo al generar la peticion de aperturaCredito");
			e.printStackTrace();
		}
		mensajeAperturaCredito.setConversationId("AperturaCredito");
		mensajeAperturaCredito.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		getLogger().info("AperturaCredito", "Enviando peticion de aperturaCredito");

		return new AperturaCreditoInitiator(this, mensajeAperturaCredito);
	}

	/**
	 * Crea el mensaje e inicia el protocolo RetiradaArticulos
	 */
	private void protocoloRetiradaArticulos() {

		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				// Si no podemos retirar un articulo, esperamos
				if (!config.getPendienteRetirada().isEmpty() && !retiradaEnMarcha) {
					retiradaEnMarcha = true;
					ACLMessage msjRetiradaArticulo = new ACLMessage(ACLMessage.REQUEST);
					msjRetiradaArticulo.addReceiver(lonja);
					try {
						msjRetiradaArticulo.setContentObject(config.getPendienteRetirada().getFirst());
					} catch (IOException e) {
						e.printStackTrace();
					}
					msjRetiradaArticulo.setConversationId("RetiradaArticulo");
					msjRetiradaArticulo.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					((POAAgent) myAgent).getLogger().info("RetiradaArticulo",
							"Retirando articulo " + config.getPendienteRetirada().getFirst());
					myAgent.addBehaviour(new RetiradaArticuloInitiator(myAgent, msjRetiradaArticulo,
							config.getPendienteRetirada().getFirst()));
				} else {
					block(1000);
				}

			}
		});
	}

	public void cambiarDinero(Double dinero) {
		config.setDinero(config.getDinero() - dinero);
	}

	public boolean interesaPescado(String pescado, double precio) {
		for (ArticuloCompra articuloCompra : config.getListaCompra()) {
			if (articuloCompra.getPescado().equals(pescado) && precio <= articuloCompra.getPrecioDispuesto()) {
				return true;
			}
		}
		return false;
	}

	public void eliminarListaCompra(Articulo articulo) {
		config.eliminarListaCompra(articulo);
	}

	public void retiradaArticulo(Articulo articulo) {
		config.eliminarArticuloRetirada(articulo);
	}

	public boolean isRetiradaEnMarcha() {
		return retiradaEnMarcha;
	}

	public void setRetiradaEnMarcha(boolean estadoRetirada) {
		this.retiradaEnMarcha = estadoRetirada;
	}

	/**
	 * Eliminamos el comportamiento del secuencial cuando ha terminado. Si no quedan
	 * más comportamientos, eliminamos el propio secuencial.
	 * 
	 * @param behaviour que queremos eliminar
	 */
	public void removeSequentialBehaviour(Behaviour behaviour) {
		seq.removeSubBehaviour(behaviour);
		if (seq.getChildren().isEmpty()) {
			removeBehaviour(seq);
		}
	}

	private Comprador initAgentFromConfigFile(String fileName) {
		Comprador config = null;
		try {
			Yaml yaml = new Yaml();
			InputStream inputStream;
			inputStream = new FileInputStream(fileName);
			config = yaml.load(inputStream);
			getLogger().info("initAgentFromConfigFile", config.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return config;
	}

}
