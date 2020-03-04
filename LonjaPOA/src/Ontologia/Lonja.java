package Ontologia;

import java.security.acl.AclNotFoundException;
import java.util.LinkedList;

import Protocolos.AdmisionVendedorP;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class Lonja extends Agent{

	private LinkedList<Vendedor> vendedores;
	private LinkedList<Comprador> comprador;
	
	public Lonja() {
		this.vendedores = new LinkedList<Vendedor>();
		this.comprador = new LinkedList<Comprador>();
	}
	
	public void setup() {
		
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
		
		/*addBehaviour(new Achi() {
			
			@Override
			public void action() {
				
				MessageTemplate msjRegistroVendedor = MessageTemplate.MatchConversationId("RegistroVendedore");
				Vendedor vendedor = null;
				vendedor = msjRegistroVendedor.get
				
			}
		});*/
		
	}
	
}
