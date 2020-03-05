package Agentes;

import java.util.LinkedList;

import Ontologia.Comprador;
import Ontologia.Vendedor;
import Protocolos.AdmisionCompradorI;
import Protocolos.AdmisionCompradorP;
import Protocolos.AdmisionVendedorP;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class AgenteLonja extends Agent {

	private LinkedList<Vendedor> vendedores;
	private LinkedList<Comprador> compradores;

	public AgenteLonja() {
		this.vendedores = new LinkedList<Vendedor>();
		this.compradores = new LinkedList<Comprador>();
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
	}

	public void addVendedor(Vendedor vendedor) {
		if (!this.vendedores.contains(vendedor)) {
			this.vendedores.add(vendedor);
		}
	}

	public boolean containsVendedor(Vendedor vendedor) {
		return this.vendedores.contains(vendedor);
	}
	
	public void addComprador(Comprador comprador) {
		if (!this.compradores.contains(comprador)) {
			this.compradores.add(comprador);
		}
	}

	public boolean containsComprador(Comprador comprador) {
		return this.compradores.contains(comprador);
	}
}
