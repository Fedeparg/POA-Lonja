package poa.agentes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;

import org.yaml.snakeyaml.Yaml;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.MessageTemplate;
import poa.ontologia.Articulo;
import poa.ontologia.Lonja;
import poa.protocolos.RegistroCompradorParticipant;
import poa.protocolos.RegistroVendedorParticipant;
import poa.protocolos.AperturaCreditoParticipant;
import poa.protocolos.CobroInitiator;
import poa.protocolos.DepositoArticuloParticipant;
import poa.protocolos.RetiradaArticuloParticipant;
import poa.protocolos.SubastaInitiator;
import jade.lang.acl.ACLMessage;

/**
 * Representación de la Lonja en Jade. Arranca sus comportamientos y realiza las
 * interacciones con los demás agentes.
 *
 */
@SuppressWarnings("serial")
public class AgenteLonja extends POAAgent {

	private Lonja config;
	private boolean subastaEnMarcha = false;
	private int estadoSubasta = 0;
	private int estadoCobro = 0;
	private boolean subastaProgramada = false;
	private boolean cobroEnMarcha = false;

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
				addBehaviour(new RegistroVendedorParticipant(this, msjRegistroVendedor));

				// Registro comprador
				MessageTemplate msjRegistroComprador = MessageTemplate.MatchConversationId("RegistroComprador");
				addBehaviour(new RegistroCompradorParticipant(this, msjRegistroComprador));

				// Deposito de articulos
				MessageTemplate msjDeposito = MessageTemplate.MatchConversationId("DepositoArticulo");
				addBehaviour(new DepositoArticuloParticipant(this, msjDeposito));

				// Apertura Credito
				MessageTemplate msjAperturaCredito = MessageTemplate.MatchConversationId("AperturaCredito");
				addBehaviour(new AperturaCreditoParticipant(this, msjAperturaCredito));

				// Subasta de articulos
				protocoloSubasta();

				// PROTOCOLO RETIRADA ARTICULO
				MessageTemplate msjRetiradaArticulo = MessageTemplate.MatchConversationId("RetiradaArticulo");
				addBehaviour(new RetiradaArticuloParticipant(this, msjRetiradaArticulo));

				// PROTOCOLO COBRO
				protocoloCobro();

			}
		} else {
			this.getLogger().info("ERROR", "Requiere fichero de cofiguración.");
			doDelete();
		}

	}

	private void protocoloSubasta() {
		addBehaviour(new CyclicBehaviour() {

			private Articulo articuloIteracion = null;

			@Override
			public void action() {
				if (estadoSubasta == 0 && !config.getArticulosParaSubastar().isEmpty() && !subastaProgramada) {
					// Si no hay una puja en marcha, se inicia tras el periodo de latencia
					((POAAgent) myAgent).getLogger().info("Subasta", "PAUSA ENTRE SUBASTAS");
					articuloIteracion = config.getArticulosParaSubastar().getFirst();
					myAgent.addBehaviour(new WakerBehaviour(myAgent, config.getPeriodoLatencia()) {
						protected void onWake() {
							estadoSubasta = 1;
						}
					});
					subastaProgramada = true;
					// Si no hay nada que hacer, dormimos el hilo
					block(config.getPeriodoLatencia() / 5);

				} else if (!config.getArticulosParaSubastar().isEmpty()) {
					
					if (articuloIteracion != null && !subastaEnMarcha && !config.getCompradores().isEmpty()
							&& estadoSubasta != 0) {
						subastaProgramada = false;
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

						// Ponemos el tiempo max para responder
						msjVendoPescado
								.setReplyByDate(new Date(System.currentTimeMillis() + config.getVentanaOportunidad()));

						((POAAgent) myAgent).getLogger().info("Subasta", "Iniciada subasta de articulo "
								+ articuloIteracion + " al precio " + articuloIteracion.getPrecio());

						myAgent.addBehaviour(new SubastaInitiator(myAgent, msjVendoPescado, articuloIteracion));
					} else {
						// Si la subasta esta en marcha, nos dormimos
						block(config.getPeriodoLatencia() / 5);
					}
				} else {
					// Si no hay articulos para subastar, dormimos el hilo
					block(config.getPeriodoLatencia());
				}
			}
		});
	}

	private void protocoloCobro() {
		addBehaviour(new CyclicBehaviour() {

			@Override
			public void action() {
				if (estadoCobro == 0) {
					myAgent.addBehaviour(new WakerBehaviour(myAgent, 500) {
						protected void onWake() {
							estadoCobro = 1;
						}
					});
					// Dormimos el hilo si ahora mismo no podemos cobrar
					block(config.getPeriodoLatencia());
				} else {
					if (!config.getArticulosCompradosNoPagados().isEmpty() && !cobroEnMarcha) {

						cobroEnMarcha = true;
						LinkedList<AID> vendedores = new LinkedList<AID>(
								config.getArticulosCompradosNoPagados().keySet());

						AID vendedor = vendedores.getFirst();

						if (!config.getArticulosCompradosNoPagados().get(vendedor).isEmpty()) {

							((POAAgent) myAgent).getLogger().info("Cobro",
									"Vamos a darle el dinero al agente " + vendedor.getLocalName());

							ACLMessage msjCobro = new ACLMessage(ACLMessage.PROPOSE);
							msjCobro.addReceiver(vendedor);
							Double dinero = config.getDineroCobro(vendedor);
							msjCobro.setContent(dinero.toString());
							LinkedList<Articulo> articulos = new LinkedList<Articulo>(
									config.getArticulosCompradosNoPagados().get(vendedor));
							msjCobro.setConversationId("Cobro");
							myAgent.addBehaviour(new CobroInitiator(myAgent, msjCobro, articulos));
						}

					} else {
						// Dormimos el hilo
						block(config.getPeriodoLatencia());
					}
				}
			}
		});

	}

	public boolean isSubastaEnMarcha() {
		return subastaEnMarcha;
	}

	public void setSubastaEnMarcha(boolean subastaEnMarcha) {
		this.subastaEnMarcha = subastaEnMarcha;
	}

	public boolean isSubastaProgramada() {
		return subastaProgramada;
	}

	public void setSubastaProgramada(boolean subastaProgramada) {
		this.subastaProgramada = subastaProgramada;
	}

	public int getEstadoSubasta() {
		return estadoSubasta;
	}

	public void setEstadoSubasta(int state) {
		this.estadoSubasta = state;
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

	/**
	 * Reduce el precio del artículo si no ha llegado a su precio de reserva.
	 * Devuelve true si se ha podido realizar la operación. False en otro caso.
	 */
	public boolean reducirPrecio(Articulo articulo) {
		articulo.setPrecio(articulo.getPrecio() - config.getDecrementoPrecio());
		if (articulo.getPrecio() < articulo.getPrecioReserva())
			return false;
		return true;
	}

	/**
	 * Devuelve True si el comprador tiene dinero para pagar el articulo por el
	 * precio actual. False en otro caso.
	 */
	public boolean suficienteDinero(AID comprador, double precio) {
		return config.getDineroComprador(comprador) >= precio;
	}

	public void imposibleVender(Articulo articulo) {
		((POAAgent) this).getLogger().info("Subasta", "Articulo " + articulo + " imposible de vender");
		config.imposibleVender(articulo);
	}

	public boolean isCobroEnMarcha() {
		return cobroEnMarcha;
	}

	public void setCobroEnMarcha(boolean cobroEnMarcha) {
		this.cobroEnMarcha = cobroEnMarcha;
	}

	public void eliminarArticulosCobrados(AID vendedor, LinkedList<Articulo> articulos) {
		config.eliminarArticulosCobrados(vendedor, articulos);
	}

	public int getEstadoCobro() {
		return estadoCobro;
	}

	public void setEstadoCobro(int estadoCobro) {
		this.estadoCobro = estadoCobro;
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

}
