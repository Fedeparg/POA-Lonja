package Agentes;

import java.io.IOException;

import Ontologia.Comprador;
import Ontologia.Vendedor;
import Protocolos.AdmisionCompradorI;
import Protocolos.AdmisionVendedorI;
import Protocolos.AperturaCreditoComprador;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteComprador extends Agent {

	private AID[] lonjas;
	private Comprador comprador;
	private AID lonja;

	public void setup() {

		System.out.println("Se ha creado " + this.getLocalName());

		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			comprador = (Comprador) args[0];
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
				mensajeRegistro.setContentObject(comprador);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mensajeRegistro.setConversationId("RegistroComprador");
			mensajeRegistro.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			System.out.println(this.getLocalName() + ": Enviando peticion de registro a lonja " + lonja.getLocalName()
					+ " como comprador");
			seq.addSubBehaviour(new AdmisionCompradorI(this, mensajeRegistro));

			// PROTOCOLO APERTURA CREDITO
			if (comprador.getDinero() > 0) {
				ACLMessage mensajeAperturaCredito = new ACLMessage(ACLMessage.REQUEST);
				mensajeAperturaCredito.addReceiver(lonja);
				try {
					mensajeAperturaCredito.setContentObject(comprador.getDinero());
				} catch (IOException e) {
					e.printStackTrace();
				}
				mensajeAperturaCredito.setConversationId("AperturaCredito");
				mensajeAperturaCredito.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				System.out.println(
						this.getLocalName() + ": Enviada solicitud de apertura credito a lonja" + lonja.getLocalName());
				seq.addSubBehaviour(new AperturaCreditoComprador(this, mensajeAperturaCredito));
			}
			addBehaviour(seq);

		}

	}

	public void cambiarDinero(Double dinero) {
		comprador.setDinero(comprador.getDinero() - dinero);
	}

}
