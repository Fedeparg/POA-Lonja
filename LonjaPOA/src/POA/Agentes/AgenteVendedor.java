package POA.Agentes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import POA.Ontologia.Articulo;
import POA.Ontologia.Vendedor;
import POA.Protocolos.AdmisionVendedorI;
import POA.Protocolos.DepositoArticuloI;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AgenteVendedor extends POAAgent {

	private AID[] lonjas;
	private Vendedor config;

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

				// Creamos un comportamiento secuencial para el registro y el deposito
				SequentialBehaviour seq = new SequentialBehaviour();

				// Enviamos el mensaje de registro
				for (AID lonja : lonjas) {
					ACLMessage mensajeRegistro = new ACLMessage(ACLMessage.REQUEST);
					mensajeRegistro.addReceiver(lonja);
					try {
						mensajeRegistro.setContentObject(config);
					} catch (IOException e) {
						System.out.println(this.getLocalName() + ": Fallo al crear el mensaje de registro");
						e.printStackTrace();
					}
					mensajeRegistro.setConversationId("RegistroVendedor");
					mensajeRegistro.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					System.out.println(
							this.getLocalName() + ": Enviando peticion de registro a lonja " + lonja.getLocalName());
					seq.addSubBehaviour(new AdmisionVendedorI(this, mensajeRegistro));
					// addBehaviour(new AdmisionVendedorI(this, mensajeRegistro));
				}

				// Enviamos los mensajes para depositar articulos en la lonja
				for (Articulo articulo : this.config.getProductosParaVender()) {
					ACLMessage mensajeDeposito = new ACLMessage(ACLMessage.REQUEST);
					mensajeDeposito.addReceiver(lonjas[0]);
					try {
						mensajeDeposito.setContentObject(articulo);
					} catch (IOException e) {
						System.out.println(this.getLocalName() + ": Fallo al crear el mensaje de deposito de articulo");
						e.printStackTrace();
					}
					mensajeDeposito.setConversationId("DepositoArticulo");
					mensajeDeposito.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					System.out.println(this.getLocalName() + ": Enviando peticion de deposito de articulo");
					seq.addSubBehaviour(new DepositoArticuloI(this, mensajeDeposito));
				}

				addBehaviour(seq);

			} else {
				doDelete();
			}
		} else {
			getLogger().info("ERROR", "Requiere fichero de cofiguración.");
			doDelete();
		}

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
}
