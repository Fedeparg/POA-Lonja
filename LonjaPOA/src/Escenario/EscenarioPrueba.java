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
			// Terminamos la m�quinq virtual si no hubiera ning�n contenedor de agentes activo
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
			
			int idArticulos = 0;
			Vendedor vendedor1 = new Vendedor(1, "AgenteVendedor1");
			Articulo articulo1 = new Articulo(idArticulos, Pescado.ATUN, 10, 50, vendedor1);
			Articulo articulo2 = new Articulo(++idArticulos, Pescado.BACALAO, 10, 50, vendedor1);
			vendedor1.addArticuloParaVender(articulo1);
			vendedor1.addArticuloParaVender(articulo2);
			Object[] argumentosVendedor1 = {vendedor1};
			AgentController agenteVendedor1 = mc.createNewAgent("AgenteVendedor1", Agentes.AgenteVendedor.class.getName(), argumentosVendedor1);
			agenteVendedor1.start();
			
			Vendedor vendedor2 = new Vendedor(2, "AgenteVendedor2");
			Object[] argumentosVendedor2 = {vendedor2};
			AgentController agenteVendedor2 = mc.createNewAgent("AgenteVendedor2", Agentes.AgenteVendedor.class.getName(), argumentosVendedor2);
			agenteVendedor2.start();
			
			Comprador comprador1= new Comprador(1, "AgenteComprador1", new LinkedList<ArticuloCompra>(), 0);
			Object[] argumentosComprador1 =  {comprador1};
			AgentController agenteComprador1 = mc.createNewAgent("AgenteComprador1", Agentes.AgenteComprador.class.getName(), argumentosComprador1);
			agenteComprador1.start();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
