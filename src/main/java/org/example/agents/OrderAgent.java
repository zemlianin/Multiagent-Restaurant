package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

// @JadeAgent
public class OrderAgent extends Agent {

    private Map<String, Integer> order = new HashMap<>();

    public static final String AGENT_TYPE = "order-cooking";

    public OrderAgent() {
        order.put("Борщ", 5);
    }

    @Override
    protected void setup() {
        order = new HashMap<>();

        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(AGENT_TYPE);
        serviceDescription.setName("JADE-order-making");

        agentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new OrderRequestsServer());
        addBehaviour(new PurchaseOrdersServer());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Printout a dismissal message
        System.out.println("Order-agent " + getAID().getName() + " terminating.");
    }

    private class OrderRequestsServer extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(messageTemplate);

            if (msg != null) {
                String dish = msg.getContent();
                ACLMessage reply = msg.createReply();

                Integer timeToCook = order.get(dish);
                if (timeToCook != null) {
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent("not-available");
                }

                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    private class PurchaseOrdersServer extends CyclicBehaviour {

        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(messageTemplate);
            if (msg != null) {
                // ACCEPT_PROPOSAL Message received. Process it
                String dish = msg.getContent();
                ACLMessage reply = msg.createReply();

                Integer price = order.remove(dish);
                if (price != null) {
                    reply.setPerformative(ACLMessage.INFORM);
                    System.out.println(dish + " cooked by cook " + msg.getSender().getName());
                } else {
                    // The requested book has been sold to another buyer in the meanwhile .
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
}
