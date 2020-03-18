package Agentes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import org.yaml.snakeyaml.Yaml;

import Ontologia.Articulo;
import Ontologia.Comprador;
import Ontologia.Lonja;
import Ontologia.Vendedor;
import Protocolos.AdmisionCompradorP;
import Protocolos.AdmisionVendedorP;
import Protocolos.AperturaCreditoLonja;
import Protocolos.DepositoArticuloP;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class AgenteLonja extends POAAgent {

	private Lonja config;

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

	public void addVendedor(AID AIDvendedor, Vendedor vendedor) {
		if (!this.config.getVendedores().containsValue(vendedor)) {
			this.config.getVendedores().put(AIDvendedor, vendedor);
		}
	}

	public boolean containsVendedor(AID aidVendedor) {
		return this.config.getVendedores().containsKey(aidVendedor);
	}

	public void addComprador(AID AIDcomprador, Comprador comprador) {
		if (!this.config.getCompradores().containsValue(comprador)) {
			this.config.getCompradores().put(AIDcomprador, comprador);
		}
	}

	public boolean containsComprador(AID aidComprador) {
		return this.config.getCompradores().containsKey(aidComprador);
	}

	public void addArticuloParaSubastar(Articulo articulo) {
		articulo.setHoraRegistro(new Date());
		this.config.getArticulosParaSubastar().add(articulo);
	}

	public void addDineroComprador(AID aidComprador, double oros) {
		config.getCompradores().get(aidComprador).setDineroLonja(oros);
	}
}
