package Agentes;

import java.io.IOException;

import Ontologia.Comprador;
import Ontologia.Vendedor;
import Protocolos.AdmisionVendedorI;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AgenteComprador extends Agent {

	private AID[] lonjas;
	private Comprador comprador;

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

			for (AID lonja : lonjas) {
				ACLMessage mensajeRegistro = new ACLMessage(ACLMessage.REQUEST);
				mensajeRegistro.addReceiver(lonja);
				try {
					mensajeRegistro.setContentObject(comprador);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mensajeRegistro.setConversationId("RegistroComprador");
				mensajeRegistro.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				System.out.println(
						this.getLocalName() + ": Enviando peticion de registro a lonja " + lonja.getLocalName());
				addBehaviour(new AdmisionVendedorI(this, mensajeRegistro));

			}

		}

	}

}
