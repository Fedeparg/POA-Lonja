package Escenario;

import java.util.LinkedList;

import Ontologia.*;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class EscenarioPrueba {

	public static void main(String[] args) {
		try {

			// Obtenemos una instancia del entorno runtime de Jade
			Runtime rt = Runtime.instance();
			// Terminamos la máquinq virtual si no hubiera ningún contenedor de agentes activo
			rt.setCloseVM(true);
			// Lanzamos una plataforma en el puerto 8888
			// Y creamos un profile de la plataforma a partir de la cual podemos
			// crear contenedores
			Profile pMain = new ProfileImpl(null, 8888, null);
			System.out.println("Lanzamos una plataforma desde clase principal..."+pMain);
			
			// Creamos el contenedor
			AgentContainer mc = rt.createMainContainer(pMain);
			
			// Creamos un RMA (la GUI de JADE)
			AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
			rma.start();
			
			// Creamos la lonja
			AgentController lonja = mc.createNewAgent("LonjaEscenarioPrueba", Agentes.AgenteLonja.class.getName(), null);
			lonja.start();
			
			// Creamos los vendedores
			Vendedor vendedor1 = new Vendedor(1, "AgenteVendedor1", new LinkedList<Articulo>());
			Object[] argumentosVendedor1 = {vendedor1};
			AgentController agenteVendedor1 = mc.createNewAgent("AgenteVendedor1", Agentes.AgenteVendedor.class.getName(), argumentosVendedor1);
			agenteVendedor1.start();
			
			Vendedor vendedor2 = new Vendedor(2, "AgenteVendedor2", new LinkedList<Articulo>());
			Object[] argumentosVendedor2 = {vendedor2};
			AgentController agenteVendedor2 = mc.createNewAgent("AgenteVendedor2", Agentes.AgenteVendedor.class.getName(), argumentosVendedor2);
			agenteVendedor2.start();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
