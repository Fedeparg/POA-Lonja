package POA.Agentes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import POA.Ontologia.ArticuloCompra;
import POA.Ontologia.Comprador;
import POA.Protocolos.AdmisionCompradorI;
import POA.Protocolos.AperturaCreditoComprador;
import POA.Protocolos.DepositoArticuloP;
import POA.Protocolos.SubastaComprador;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class AgenteComprador extends POAAgent {

	private AID[] lonjas;
	private Comprador config;
	private AID lonja;

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
				lonja = lonjas[0];

				SequentialBehaviour seq = new SequentialBehaviour();
				// PROTOCOLO REGISTRO
				ACLMessage mensajeRegistro = new ACLMessage(ACLMessage.REQUEST);
				mensajeRegistro.addReceiver(lonja);
				try {
					mensajeRegistro.setContentObject(config);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mensajeRegistro.setConversationId("RegistroComprador");
				mensajeRegistro.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				System.out.println(this.getLocalName() + ": Enviando peticion de registro a lonja "
						+ lonja.getLocalName() + " como comprador");
				seq.addSubBehaviour(new AdmisionCompradorI(this, mensajeRegistro));

				// PROTOCOLO APERTURA CREDITO
				if (config.getDinero() > 0) {
					ACLMessage mensajeAperturaCredito = new ACLMessage(ACLMessage.REQUEST);
					mensajeAperturaCredito.addReceiver(lonja);
					try {
						mensajeAperturaCredito.setContentObject(config.getDinero());
					} catch (IOException e) {
						e.printStackTrace();
					}
					mensajeAperturaCredito.setConversationId("AperturaCredito");
					mensajeAperturaCredito.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					System.out.println(this.getLocalName() + ": Enviada solicitud de apertura credito a lonja"
							+ lonja.getLocalName());
					seq.addSubBehaviour(new AperturaCreditoComprador(this, mensajeAperturaCredito));
				}
				addBehaviour(seq);

				// PROTOCOLO SUBASTA

				MessageTemplate msjPuja = MessageTemplate.MatchConversationId("Subasta");
				addBehaviour(new SubastaComprador(this, msjPuja));
			} else {
				doDelete();
			}
		} else {
			getLogger().info("ERROR", "Requiere fichero de cofiguración.");
			doDelete();
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
	
	public void eliminarListaCompra(String pescado, double cantidad) {
		config.eliminarListaCompra(pescado, cantidad);
	}
	

}
