package examples.kbb.project;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;
import net.sf.clipsrules.jni.*;

public class SellerAgent extends Agent {
    /*El catalogo de libros para ventas (mapeo el titulo de un libro a su precio)*/ 
	private ConexionBaseDeDatosAmazon bdAmazon;
	private ConexionBaseDeDatosAlibaba bdAlibaba;
	private ConexionBaseDeDatosBarnes_noble bdBarnes_noble;
	private Environment clipsAmazon;
	private Environment clipsAlibaba;
	private Environment clipsBarnes;

    //interfaz grafica de usuario por significado de los cuales el usuario puede añadir libros al catalogo
	//private BookSellerGui myGui;

	// Put agent initializations here
	protected void setup() {
		// Create the catalogue
		String cadena = (String) getAID().getName();
		if(cadena.equals("AmazonAgent@169.254.170.94:1099/JADE")){
			bdAmazon = new ConexionBaseDeDatosAmazon();
			clipsAmazon=new Environment();
			System.out.println("Agente vendedor de amazon: "+getAID().getName()+" a su base de datos y a clips.");
			clipsAmazon.load("C:\\CLIPS 6.31\\clips_jni_051\\test\\src\\examples\\kbb\\project\\templatesAmazon.clp");
			clipsAmazon.load("C:\\CLIPS 6.31\\clips_jni_051\\test\\src\\examples\\kbb\\project\\factsAmazon.clp");
		}
		else if(cadena.equals("AlibabaAgent@169.254.170.94:1099/JADE")){
			bdAlibaba = new ConexionBaseDeDatosAlibaba();
			clipsAlibaba = new Environment();
			System.out.println("Agente vendedor de alibaba: "+getAID().getName()+" a su base de datos y a clips.");
			clipsAlibaba.load("C:\\CLIPS 6.31\\clips_jni_051\\test\\src\\examples\\kbb\\project\\templatesAlibaba.clp");
			clipsAlibaba.load("C:\\CLIPS 6.31\\clips_jni_051\\test\\src\\examples\\kbb\\project\\factsAlibaba.clp");
		}
		else{
			bdBarnes_noble = new ConexionBaseDeDatosBarnes_noble();
			clipsBarnes = new Environment();
			System.out.println("Agente vendedor de barnes & noble: "+getAID().getName()+" a su base de datos y a clips.");
			clipsBarnes.load("C:\\CLIPS 6.31\\clips_jni_051\\test\\src\\examples\\kbb\\project\\templatesBarnes.clp");
			clipsBarnes.load("C:\\CLIPS 6.31\\clips_jni_051\\test\\src\\examples\\kbb\\project\\factsBarnes.clp");
		}
		// Create and show the GUI 
		/*myGui = new BookSellerGui(this);
		myGui.showGui();*/
        
        // Registrar el servicio de libros de ventas en paginas amarillas
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("product-selling");
		sd.setName("Kbb Project Product-selling");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

        //añadir el servicio de comportamiento requerido desde agentes compradores
		addBehaviour(new OfferRequestsServer());

        //añadir el servicio de comportamiento ordenes de compra desde un agente comprador
		addBehaviour(new PurchaseOrdersServer());
	}

    //poner operaciones de agentes limpiadores aqui
	protected void takeDown() {
		String cadena=(String) getAID().getName();
        //Dar de baja desde paginas amarillas
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		if(cadena.equals("AmazonAgent@169.254.170.94:1099/JADE")){
			System.out.println("Seller-agent "+cadena+" terminating.");
			clipsAmazon.eval("(exit)");
		}
		else if(cadena.equals("AlibabaAgent@169.254.170.94:1099/JADE")){
			System.out.println("Seller-agent "+cadena+" terminating.");
			clipsAlibaba.eval("(exit)");
		}
		else{
			System.out.println("Seller-agent "+cadena+" terminating.");
			clipsBarnes.eval("(exit)");
		}
		doDelete();
	}


    /*Clase interna OfferRequestsServer.
      Esto es el comportamiento usado por agentes vendedores de libro para servir pedidos entrantes
      por oferta desde un agente comprador
      Si el pedido del libro es dentro del catalogo del vendedor responde con un mensaje PROPOSE especificando el price. De lo contrario
      un mensaje de rechaso es enviado de vuelta*/
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			String cadena = (String) getAID().getName();
			String nombreDelCustomer="";
			char c=(char)34;
			String comillas = String.valueOf(c);
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			boolean productoEncontrado=false;
			if (msg != null) {
				String query="";
				String name = msg.getContent();
				nombreDelCustomer=msg.getSender().getName();
				ACLMessage reply = msg.createReply();
				query="SELECT * FROM product WHERE name='" + name + "' AND quantity >= " + 1 ;
				if(cadena.equals("AmazonAgent@169.254.170.94:1099/JADE"))
				{
					productoEncontrado=bdAmazon.realizarConsulta(query);
					if(productoEncontrado){
						if(nombreDelCustomer.equals("CustomerAgent1@169.254.170.94:1099/JADE")){
							clipsAmazon.build("(deffacts customersAmazon (customerAmazon (name " +nombreDelCustomer+ ") (targeta Liverpool VISA)))");
							clipsAmazon.build("(deffacts ordersAmazon (orderAmazon (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						else if(nombreDelCustomer.equals("CustomerAgent2@169.254.170.94:1099/JADE")){
							clipsAmazon.build("(deffacts customersAmazon (customerAmazon (name " +nombreDelCustomer+ ") (targeta Banamex)))");
							clipsAmazon.build("(deffacts ordersAmazon (orderAmazon (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						else{
							clipsAmazon.build("(deffacts customersAmazon (customerAmazon (name " +nombreDelCustomer+ ") (targeta none)))");
							clipsAmazon.build("(deffacts ordersAmazon (orderAmazon (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						clipsAmazon.eval("(reset)");
						clipsAmazon.build("(defrule 12-meses (productAmazon (name Samsung Note 12)) (customerAmazon (name ?n) (targeta Liverpool VISA))(orderAmazon (nameCustomer "+nombreDelCustomer+") (nameProduct "+name+")) => (printout t "+comillas+"El producto "+name+", tiene 12 meses sin intereses con el pago de targeta Liverpool VISA"+comillas+" crlf))");
						clipsAmazon.build("(defrule descuento-15-porciento (productAmazon (category smartphone)) (customerAmazon (name ?n) (targeta ?ta))(orderAmazon (nameCustomer "+nombreDelCustomer+") (nameProduct "+name+")) => (printout t "+comillas+"Tenemos una funda y un mica con un 15% de descuento en la compra de un smartphone " +name+ "" +comillas+" crlf))");	
					}
					
				}
				else if(cadena.equals("AlibabaAgent@169.254.170.94:1099/JADE"))
				{
					productoEncontrado=bdAlibaba.realizarConsulta(query);
					if(productoEncontrado){
						if(nombreDelCustomer.equals("CustomerAgent1@169.254.170.94:1099/JADE")){
							clipsAlibaba.build("(deffacts customersAlibaba (customerAlibaba (name " +nombreDelCustomer+ ") (targeta Liverpool VISA)))");
							clipsAlibaba.build("(deffacts ordersAlibaba (orderAlibaba (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						else if(nombreDelCustomer.equals("CustomerAgent2@169.254.170.94:1099/JADE")){
							clipsAlibaba.build("(deffacts customersAlibaba (customerAlibaba (name " +nombreDelCustomer+ ") (targeta Banamex)))");
							clipsAlibaba.build("(deffacts ordersAlibaba (orderAlibaba (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						else{
							clipsAlibaba.build("(deffacts customersAlibaba (customerAlibaba (name " +nombreDelCustomer+ ") (targeta none)))");
							clipsAlibaba.build("(deffacts ordersAlibaba (orderAlibaba (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						clipsAlibaba.eval("(reset)");
						clipsAlibaba.build("(defrule oferta100-pesosValesPor-1000-deCompra (productAlibaba(name MacBook Air) (category laptops)) (customerAlibaba(name ?n) (targeta none)) (orderAlibaba(nameCustomer "+nombreDelCustomer+")(nameProduct MacBook Air)) => (printout t "+comillas+"Al pagar a contado el producto "+name+" le ofrecemos 100 pesos en VALES por cada 1000 pesos de COMPRA"+comillas+" crlf))");
					    clipsAlibaba.build("(defrule descuento-15-porciento (productAlibaba (name "+name+")(category smartphone)) (customerAlibaba (name ?n) (targeta ?ta))(orderAlibaba (nameCustomer "+nombreDelCustomer+") (nameProduct "+name+")) => (printout t "+comillas+"Tenemos una funda y un mica con un 15% de descuento en la compra de un smartphone " +name+ "" +comillas+" crlf))");
					}
				}
				else
				{
					productoEncontrado=bdBarnes_noble.realizarConsulta(query);
					if(productoEncontrado){
						if(nombreDelCustomer.equals("CustomerAgent1@169.254.170.94:1099/JADE")){
							clipsBarnes.build("(deffacts customersBarnes (customerBarnes (name " +nombreDelCustomer+ ") (targeta Liverpool VISA)))");
							clipsBarnes.build("(deffacts ordersBarnes (orderBarnes (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						else if(nombreDelCustomer.equals("CustomerAgent2@169.254.170.94:1099/JADE")){
							clipsBarnes.build("(deffacts customersBarnes (customerBarnes (name " +nombreDelCustomer+ ") (targeta Banamex)))");
							clipsBarnes.build("(deffacts ordersBarnes (orderBarnes (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						else{
							clipsBarnes.build("(deffacts customersBarnes (customerBarnes (name " +nombreDelCustomer+ ") (targeta none)))");
							clipsBarnes.build("(deffacts ordersBarnes (orderBarnes (nameCustomer "+nombreDelCustomer+ ") (nameProduct "+name+")))");
						}
						clipsBarnes.eval("(reset)");
						clipsBarnes.build("(defrule 24-meses (productBarnes (name iPhone 12 Pro Max)) (customerBarnes (name ?n) (targeta Banamex))(orderBarnes (nameCustomer "+nombreDelCustomer+") (nameProduct "+name+")) => (printout t "+comillas+"El producto "+name+", tiene 12 meses sin intereses con el pago de targeta Banamex"+comillas+" crlf))");
						clipsBarnes.build("(defrule descuento-15-porciento (productBarnes (name "+name+")(category smartphone)) (orderBarnes (nameCustomer "+nombreDelCustomer+") (nameProduct "+name+")) => (printout t "+comillas+"Tenemos una funda y un mica con un 15% de descuento en la compra de un smartphone " +name+ "" +comillas+" crlf))");
						
					}
				}
				if (name != null && productoEncontrado){
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(name);
				}
				else {
                    //La solicitud del producto no esta disponible para venta
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer

	
    /*Clase interna PurchaseOrderServer.
      Este es un comportamiento usado por agente vendedor de libros para atender ofertas aceptadas
      desde el agente comprador
      El agente vendedor remueve el libro adquirido desde el catalogo y responde con un mensaje informando para notificar
      al comprador que su adquicicion ha sido completado*/
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			String cadena = (String) getAID().getName();
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			boolean productoVendido;
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				ACLMessage reply = msg.createReply();
				String name=msg.getContent();
				String query="SELECT * FROM product WHERE name='" + name + "' AND quantity >= " + 1 ;

				if(cadena.equals("AmazonAgent@169.254.170.94:1099/JADE")){
					productoVendido = bdAmazon.actualizarCatalogo(query);
					clipsAmazon.run();
				}
				else if(cadena.equals("AlibabaAgent@169.254.170.94:1099/JADE")){
					productoVendido=bdAlibaba.actualizarCatalogo(query);
					clipsAlibaba.run();
				}
				else{
					productoVendido=bdBarnes_noble.actualizarCatalogo(query);
					clipsBarnes.run();
					
				}
				if(name!=null && productoVendido){
					reply.setPerformative(ACLMessage.INFORM);
					System.out.println( "El producto "+name+" ha sido vendido al customer "+msg.getSender().getName());
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer
}
