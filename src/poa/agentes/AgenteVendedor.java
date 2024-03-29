package poa.agentes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.yaml.snakeyaml.Yaml;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import poa.ontologia.Articulo;
import poa.ontologia.Vendedor;
import poa.protocolos.RegistroVendedorInitiator;
import poa.protocolos.CobroParticipant;
import poa.protocolos.DepositoArticuloInitiator;

/**
 * Representación del vendedor en Jade. Arranca sus comportamientos y realiza
 * las interacciones con los demás agentes.
 *
 */
@SuppressWarnings("serial")
public class AgenteVendedor extends POAAgent {

	private AID lonja;
	private Vendedor config;
	// Creamos un comportamiento secuencial para el registro y el deposito
	SequentialBehaviour comportamientoSecuencial = new SequentialBehaviour(this);

	public void setup() {
		super.setup();

		Object[] args = getArguments();
		if (args != null && args.length == 1) {
			Path configg = (Path) args[0];
			String configFile = configg.toString();
			config = initAgentFromConfigFile(configFile);

			if (config != null) {
				lonja = buscaLonjas();

				// PROTOCOLO REGISTRO VENDEDOR
				comportamientoSecuencial.addSubBehaviour(protocoloRegistroComprador());

				// PROTOCOLO DEPOSITO DE ARTICULO
				for (Articulo articulo : this.config.getProductosParaVender()) {
					comportamientoSecuencial.addSubBehaviour(protocoloDepositoArticulo(articulo));
				}

				addBehaviour(comportamientoSecuencial);

				// PROTOCOLO COBRO
				MessageTemplate msjCobro = MessageTemplate.MatchConversationId("Cobro");
				addBehaviour(new CobroParticipant(this, msjCobro));

			} else {
				doDelete();
			}
		} else {
			getLogger().info("ERROR", "Requiere fichero de cofiguración.");
			doDelete();
		}
	}

	private AID buscaLonjas() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("lonja");
		sd.setType("lonja");
		template.addServices(sd);
		DFAgentDescription[] result;
		AID[] listaLonjas = null;
		do {
			try {
				result = DFService.search(this, template);
				listaLonjas = new AID[result.length];
				for (int i = 0; i < result.length; i++) {
					listaLonjas[i] = result[i].getName();
				}
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		} while (listaLonjas.length == 0);
		lonja = listaLonjas[0];
		return lonja;
	}

	/**
	 * Crea el mensaje e inicia el protocolo RegistroVendedor
	 */
	private Behaviour protocoloRegistroComprador() {
		ACLMessage mensajeRegistro = new ACLMessage(ACLMessage.REQUEST);
		mensajeRegistro.addReceiver(lonja);
		try {
			mensajeRegistro.setContentObject(config);
		} catch (IOException e) {
			getLogger().info("AdmisionVendedor", "Fallo al generar la peticion de registro del agente");
			e.printStackTrace();
		}
		mensajeRegistro.setConversationId("RegistroVendedor");
		mensajeRegistro.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		getLogger().info("AdmisionVendedor", "Enviando peticion de registro");

		return new RegistroVendedorInitiator(this, mensajeRegistro);
	}

	/**
	 * Crea el mensaje e inicia el protocolo RegistroVendedor
	 */
	private Behaviour protocoloDepositoArticulo(Articulo articulo) {
		ACLMessage mensajeDeposito = new ACLMessage(ACLMessage.REQUEST);
		mensajeDeposito.addReceiver(lonja);
		try {
			mensajeDeposito.setContentObject(articulo);
		} catch (IOException e) {
			getLogger().info("DepositoArticulo", "Fallo al crear la petición de depósito del articulo  " + articulo);
			e.printStackTrace();
		}
		mensajeDeposito.setConversationId("DepositoArticulo");
		mensajeDeposito.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		getLogger().info("DepositoArticulo", "Enviando peticion de deposito de articulo " + articulo);
		return new DepositoArticuloInitiator(this, mensajeDeposito);
	}

	/**
	 * Eliminamos el comportamiento del secuencial cuando ha terminado. Si no quedan
	 * más comportamientos, eliminamos el propio secuencial.
	 * 
	 * @param behaviour que queremos eliminar
	 */
	public void removeSequentialBehaviour(Behaviour behaviour) {
		comportamientoSecuencial.removeSubBehaviour(behaviour);
		if (comportamientoSecuencial.getChildren().isEmpty()) {
			removeBehaviour(comportamientoSecuencial);
		}
	}

	public void addGanancias(Double dinero) {
		config.setGanancias(config.getGanancias() + dinero);
	}

	private Vendedor initAgentFromConfigFile(String fileName) {
		Vendedor config = null;
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

	public void takeDown() {
		super.takeDown();
	}
}
