package poa.escenario;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.yaml.snakeyaml.Yaml;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.sniffer.Sniffer;
import jade.util.Logger;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import poa.utils.AgentLoggingHTMLFormatter;

/**
 * 
 * Contiene el script para cargar el escenario e iniciar los agentes
 * 
 */
public class Escenario {

	public static void main(String[] args) throws SecurityException, IOException {

		// LOS AGENTES EN EL SNIFFER
		List<String> simulationAgents = new LinkedList<String>();

		if (args.length == 1) {
			String config_file = args[0];
			Path config_path = FileSystems.getDefault().getPath(args[0]).normalize().toAbsolutePath().getParent();
			System.out.println(config_path);
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
				Profile pMain = new ProfileImpl(null, 8888, null);
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
				System.out.println("THIS" + marketConfig);
				System.out.println(Paths.get(config_path.toString(), marketConfig.getConfig()));
				Object[] marketConfigArg = { Paths.get(config_path.toString(), marketConfig.getConfig()) };
				simulationAgents.add(marketConfig.getName());
				AgentController market = mc.createNewAgent(marketConfig.getName(),
						poa.agentes.AgenteLonja.class.getName(), marketConfigArg);
				market.start();

				// Buyers
				List<AgentRefConfig> buyers = scenario.getBuyers();
				if (buyers != null) {
					for (AgentRefConfig buyer : buyers) {
						System.out.println(buyer);
						simulationAgents.add(buyer.getName());
						Object[] buyerConfigArg = { Paths.get(config_path.toString(), buyer.getConfig()) };
						AgentController b = mc.createNewAgent(buyer.getName(),
								poa.agentes.AgenteComprador.class.getName(), buyerConfigArg);
						b.start();
					}
				}

				// Sellers
				List<AgentRefConfig> sellers = scenario.getSellers();
				if (sellers != null) {
					for (AgentRefConfig seller : sellers) {
						System.out.println(seller);
						simulationAgents.add(seller.getName());
						Object[] sellerConfigArg = { Paths.get(config_path.toString(), seller.getConfig()) };
						AgentController b = mc.createNewAgent(seller.getName(),
								poa.agentes.AgenteVendedor.class.getName(), sellerConfigArg);
						b.start();
					}
				}

				addSniffer(mc, simulationAgents);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void initLogging(String scenarioName) throws SecurityException, IOException {
		LogManager lm = LogManager.getLogManager();

		Logger logger = Logger.getMyLogger("poa");
		logger.setLevel(Level.INFO);

		File carpetaLog = new File("logs");
		if (!carpetaLog.exists()) {
			carpetaLog.mkdir();
		}
		FileHandler html_handler = new FileHandler("logs/" + scenarioName + ".html");
		html_handler.setFormatter(new AgentLoggingHTMLFormatter());
		logger.addHandler(html_handler);

		lm.addLogger(logger);
	}

	/**
	 * Metodo para incluir el agente sniffer al contenedor principal de agentes.
	 * 
	 * @param mc     Contenedor principal de agentes.
	 * @param agents List<String> con los agentes a incluir en el sniffer.
	 * @throws Exception
	 */
	private static void addSniffer(AgentContainer mc, List<String> agents) throws Exception {
		// Array de argumentos para el sniffer, contiene los nombres de los agentes
		// sobre
		agents.add("df");
		Object[] arguments = { String.join(";", agents) };
		AgentController sniffer = mc.createNewAgent("snifferAgent", Sniffer.class.getName(), arguments);
		sniffer.start();

	}

}
