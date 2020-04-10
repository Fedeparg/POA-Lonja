package poa.escenario;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.yaml.snakeyaml.Yaml;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.Logger;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import poa.utils.AgentLoggingHTMLFormatter;

public class EscenarioPrueba {

	public static void main(String[] args) throws SecurityException, IOException {
		if (args.length == 1) {
			String config_file = args[0];
			Yaml yaml = new Yaml();
			InputStream inputStream = new FileInputStream(config_file);
			ScenarioConfig scenario = yaml.load(inputStream);

			initLogging(scenario.getName());

			System.out.println(scenario);
			try {

				// Obtenemos una instancia del entorno runtime de Jade
				Runtime rt = Runtime.instance();

				// Terminamos la máquinq virtual si no hubiera ningún contenedor de agentes
				// activo
				rt.setCloseVM(true);

				// Lanzamos una plataforma en el puerto 8888
				// Y creamos un profile de la plataforma a partir de la cual podemos
				// crear contenedores
				Profile pMain = new ProfileImpl(null, 10000, null);
				System.out.println("Lanzamos una plataforma desde clase principal... " + pMain);

				// Creamos el contenedor
				AgentContainer mc = rt.createMainContainer(pMain);

				// Creamos un RMA (la GUI de JADE)
				System.out.println("Lanzando el agente RMA en el contenedor main ...");
				AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
				rma.start();

				// INICIALIZACIÓN DE LOS AGENTES

				// FishMarket
				AgentRefConfig marketConfig = scenario.getFishMarket();
				Object[] marketConfigArg = { marketConfig.getConfig() };
				AgentController market = mc.createNewAgent(marketConfig.getName(),
						poa.agentes.AgenteLonja.class.getName(), marketConfigArg);
				market.start();

				// Buyers
				List<AgentRefConfig> buyers = scenario.getBuyers();
				for (AgentRefConfig buyer : buyers) {
					System.out.println(buyer);
					Object[] buyerConfigArg = { buyer.getConfig() };
					AgentController b = mc.createNewAgent(buyer.getName(), poa.agentes.AgenteComprador.class.getName(),
							buyerConfigArg);
					b.start();
				}

				// Sellers
				List<AgentRefConfig> sellers = scenario.getSellers();
				for (AgentRefConfig seller : sellers) {
					System.out.println(seller);
					Object[] buyerConfigArg = { seller.getConfig() };
					AgentController b = mc.createNewAgent(seller.getName(), poa.agentes.AgenteVendedor.class.getName(),
							buyerConfigArg);
					b.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void initLogging(String scenarioName) throws SecurityException, IOException {
		LogManager lm = LogManager.getLogManager();

		Logger logger = Logger.getMyLogger("poa");
		logger.setLevel(Level.INFO);

		FileHandler html_handler = new FileHandler("logs/" + scenarioName + ".html");
		html_handler.setFormatter(new AgentLoggingHTMLFormatter());
		logger.addHandler(html_handler);

		lm.addLogger(logger);
	}

}