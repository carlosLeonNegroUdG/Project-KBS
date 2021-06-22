package examples.kbb.project;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;

public class SupplierAgent extends Agent{

    protected void setup(){
        
        DFAgentDescription dfdp = new DFAgentDescription();
		dfdp.setName(getAID());
		ServiceDescription sdp = new ServiceDescription();
		sdp.setType("product-suplier");
		sdp.setName("Kbb Project Product-Suplier");
		dfdp.addServices(sdp);
		try {
			DFService.register(this, dfdp);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

    }

    protected void takeDown() {
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Suplier-agent "+getAID().getName()+" terminating.");
	}
   // private class 


}