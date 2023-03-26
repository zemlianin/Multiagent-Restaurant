package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.JadeAgent;
import org.example.models.Order;
import org.example.util.ACLMessageUtil;
import org.example.util.JsonMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JadeAgent("OrderAgent")
public class OrderAgent extends Agent {

    private ArrayList<Order> orders = new ArrayList<>();

    public static final String AGENT_TYPE = "OrderAgent";

    public OrderAgent() {
    }

    @Override
    protected void setup() {

        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(AGENT_TYPE);
        serviceDescription.setName("OrderAgent");

        agentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new OrderRequestsServer());
        //  addBehaviour(new PurchaseOrdersServer());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Printout a dismissal message
        System.out.println("Order-agent " + getAID().getName() + " terminating.");
    }

    private class OrderRequestsServer extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {

                var order = ACLMessageUtil.getContent(msg, Order.class);
                JsonMessage cfp = new JsonMessage(ACLMessage.CFP);
                orders.add(order);
                System.out.println("Новый список текущих заказов:" + orders);

                AID[] recipients = getCookAgent();
                for (AID recipient : recipients) {
                    cfp.addReceiver(recipient);
                }
                cfp.setContent(order);
                myAgent.send(cfp);
                System.out.println("Заказ отправлен");
            } else {
                block();
            }
        }

        private AID[] getCookAgent() {

            ArrayList<AID> orderAgens = new ArrayList<>();
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription serviceDescription = new ServiceDescription();


            serviceDescription.setType(CookAgent.AGENT_TYPE);
            template.addServices(serviceDescription);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                for (DFAgentDescription agentDescription : result) {
                    orderAgens.add(agentDescription.getName());
                }
            } catch (FIPAException ex) {
                ex.printStackTrace();
            }
            return orderAgens.toArray(new AID[0]);

        }
    }

    private class PurchaseOrdersServer extends CyclicBehaviour {

        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(messageTemplate);
            if (msg != null) {

            } else {
                block();
            }
        }
    }
}
