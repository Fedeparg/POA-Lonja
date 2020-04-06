package POA.Agentes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.yaml.snakeyaml.Yaml;

import POA.Ontologia.Articulo;
import POA.Ontologia.Lonja;
import POA.Protocolos.AdmisionCompradorP;
import POA.Protocolos.AdmisionVendedorP;
import POA.Protocolos.AperturaCreditoLonja;
import POA.Protocolos.DepositoArticuloP;
import POA.Protocolos.SubastaLonja;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AgenteLonja extends POAAgent {

	private Lonja config;
	private boolean subastaEnMarcha = false;

	public void setup() {
		super.setup();
		Object[] args = getArguments();
		if (args != null && args.length == 1) {
			String configFile = (String) args[0];
			config = initAgentFromConfigFile(configFile);

			if (config != null) {
				// Registrar el servicio en las paginas amarillas
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				ServiceDescription sd = new ServiceDescription();
				sd.setType("lonja");
				sd.setName("lonja");
				dfd.addServices(sd);
				try {
					DFService.register(this, dfd);
				} catch (FIPAException e) {
					e.printStackTrace();
				}

				// Registro vendedor
				MessageTemplate msjRegistroVendedor = MessageTemplate.MatchConversationId("RegistroVendedor");
				addBehaviour(new AdmisionVendedorP(this, msjRegistroVendedor));

				// Registro comprador
				MessageTemplate msjRegistroComprador = MessageTemplate.MatchConversationId("RegistroComprador");
				addBehaviour(new AdmisionCompradorP(this, msjRegistroComprador));

				// Deposito de articulos
				MessageTemplate msjDeposito = MessageTemplate.MatchConversationId("DepositoArticulo");
				addBehaviour(new DepositoArticuloP(this, msjDeposito));

				// Apertura Credito
				MessageTemplate msjAperturaCredito = MessageTemplate.MatchConversationId("AperturaCredito");
				addBehaviour(new AperturaCreditoLonja(this, msjAperturaCredito));

				// Vendemos cosas
				addBehaviour(new CyclicBehaviour() {
					private int state = 0;
					private Articulo articuloIteracion = null;

					@Override
					public void action() {
						if (state == 0) {
							((POAAgent) myAgent).getLogger().info("Subasta", "PAUSA ENTRE SUBASTAS");
							block(config.getPeriodoLatencia());
							state++;
						} else {
							if (!config.getArticulosParaSubastar().isEmpty()) {
								if (articuloIteracion != null && articuloIteracion.getComprador() != null) {
									articuloIteracion = config.getArticulosParaSubastar().getFirst();
									state = 0;
								} else {
									articuloIteracion = config.getArticulosParaSubastar().getFirst();
								}
							} else {
								articuloIteracion = null;
							}
							if (articuloIteracion != null && !subastaEnMarcha && !config.getCompradores().isEmpty()
									&& state != 0) {
								subastaEnMarcha = true;
								ACLMessage msjVendoPescado = new ACLMessage(ACLMessage.CFP);
								for (AID aidComprador : config.getCompradores()) {
									msjVendoPescado.addReceiver(aidComprador);
								}
								try {
									msjVendoPescado.setContentObject(articuloIteracion);
								} catch (IOException e) {
									e.printStackTrace();
								}
								msjVendoPescado.setConversationId("Subasta");
								msjVendoPescado.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
								msjVendoPescado.setReplyByDate(
										new Date(System.currentTimeMillis() + config.getVentanaOportunidad()));
								((POAAgent) myAgent).getLogger().info("Subasta", "Iniciada subasta de articulo"
										+ articuloIteracion + "al precio " + articuloIteracion.getPrecio());
								myAgent.addBehaviour(new SubastaLonja(myAgent, msjVendoPescado, articuloIteracion));
							}

						}
					}
				});
			}
		} else {
			this.getLogger().info("ERROR", "Requiere fichero de cofiguración.");
			doDelete();
		}

	}

	private Lonja initAgentFromConfigFile(String fileName) {
		Lonja config = null;
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

	public boolean isSubastaEnMarcha() {
		return subastaEnMarcha;
	}

	public void setSubastaEnMarcha(boolean subastaEnMarcha) {
		this.subastaEnMarcha = subastaEnMarcha;
	}

	public void addVendedor(AID AIDvendedor) {
		if (!containsVendedor(AIDvendedor)) {
			this.config.getVendedores().add(AIDvendedor);
		}
	}

	public boolean containsVendedor(AID AIDVendedor) {
		return this.config.getVendedores().contains(AIDVendedor);
	}

	public void addComprador(AID AIDcomprador) {
		if (!containsComprador(AIDcomprador)) {
			this.config.setDineroComprador(AIDcomprador, 0.0);
		}
	}

	public boolean containsComprador(AID aidComprador) {
		return this.config.getCompradores().contains(aidComprador);
	}

	public void addArticuloParaSubastar(Articulo articulo, AID vendedor) {
		articulo.setHoraRegistro(new Date());
		this.config.addArticuloParaSubastar(articulo, vendedor);
	}

	public void addDineroComprador(AID aidComprador, double oros) {
		this.config.setDineroComprador(aidComprador, oros);
	}

	public void articuloVendido(Articulo articulo, AID comprador) {
		articulo.setHoraVenta(new Date());
		articulo.setComprador(comprador);
		config.articuloVendido(articulo, comprador);
	}

	public boolean reducirPrecio(Articulo articulo) {
		articulo.setPrecio(articulo.getPrecio() - config.getDecrementoPrecio());
		if (articulo.getPrecio() < articulo.getPrecioReserva())
			return false;
		return true;
	}

	public boolean suficienteDinero(AID comprador, double precio) {
		return config.getDineroComprador(comprador) > precio;
	}

	public void imposibleVender(Articulo articulo) {
		((POAAgent) this).getLogger().info("Subasta",
				"Articulo " + articulo + " imposible de vender");
		config.imposibleVender(articulo);
	}
}
