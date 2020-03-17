package Agentes;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import Ontologia.Articulo;
import Ontologia.Comprador;
import Ontologia.Vendedor;
import Protocolos.AdmisionCompradorP;
import Protocolos.AdmisionVendedorP;
import Protocolos.AperturaCreditoLonja;
import Protocolos.DepositoArticuloP;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class AgenteLonja extends Agent {
	
	private HashMap<AID, Vendedor> vendedores;
	private HashMap<AID, Comprador> compradores;
	private LinkedList<Articulo> articulosParaSubastar;
	private LinkedList<Articulo> articulosCompradosNoPagados;
	private LinkedList<Articulo> articulosImposiblesDeVender;


	public AgenteLonja() {
		this.vendedores = new HashMap<AID, Vendedor>();
		this.compradores = new HashMap<AID, Comprador>();
		this.articulosParaSubastar = new LinkedList<Articulo>();
		this.articulosCompradosNoPagados = new LinkedList<Articulo>();
		this.articulosImposiblesDeVender = new LinkedList<Articulo>();
	}

	public void setup() {

		System.out.println("Se ha creado " + this.getLocalName());
		
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
		addBehaviour(new DepositoArticuloP (this, msjDeposito));
		
		// Apertura Credito
		MessageTemplate msjAperturaCredito = MessageTemplate.MatchConversationId("AperturaCredito");
		addBehaviour(new AperturaCreditoLonja(this, msjAperturaCredito));
	}

	public void addVendedor(AID AIDvendedor, Vendedor vendedor) {
		if (!this.vendedores.containsValue(vendedor)) {
			this.vendedores.put(AIDvendedor, vendedor);
		}
	}

	public boolean containsVendedor(AID aidVendedor) {
		return this.vendedores.containsKey(aidVendedor);
	}
	
	public void addComprador(AID AIDcomprador, Comprador comprador) {
		if (!this.compradores.containsValue(comprador)) {
			this.compradores.put(AIDcomprador, comprador);
		}
	}

	public boolean containsComprador(AID aidComprador) {
		return this.compradores.containsKey(aidComprador);
	}

	public void addArticuloParaSubastar(Articulo articulo) {
		articulo.setHoraRegistro(new Date());
		this.articulosParaSubastar.add(articulo);
	}
	
	public void addDineroComprador(AID aidComprador, double oros) {
		compradores.get(aidComprador).setDineroLonja(oros);
	}
}
