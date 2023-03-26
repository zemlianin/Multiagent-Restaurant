package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.JadeAgent;
import org.example.models.Dish;

import java.util.List;

@JadeAgent
public class KitchenMachineAgent extends Agent {
    // The Dish that machine can cook
    //private List<Dish> targetDishes;
    private Dish targetDish;
    // The list of known orders agents
    private List<AID> OrderAgents;

    @Override
    protected void setup() {
        // Printout a welcome message
        // System.out.println("Hello! Cook-agent " + getAID().getName() + " is ready.");
        System.out.println("- Птичка, Птичка, я Машина " + getAID().getName() + ", как слышно ? Приём");
        // Get the dishes that this cook can make
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetDish = (Dish) args[0];
            System.out.println("This machine can cook following dish: " + targetDish.getName());

            addBehaviour(new TickerBehaviour(this, 60000) {
                @Override
                protected void onTick() {
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription serviceDescription = new ServiceDescription();

                    serviceDescription.setType(BookSellerAgent.AGENT_TYPE);
                    template.addServices(serviceDescription);
                    try {
                        DFAgentDescription[] result = DFService.search(myAgent, template);

                        for (DFAgentDescription agentDescription : result) {
                            OrderAgents.add(agentDescription.getName());
                        }
                    } catch (FIPAException ex) {
                        ex.printStackTrace();
                    }

                    myAgent.addBehaviour(new RequestPerformer());
                }
            });
        } else {
            // Make the agent terminate immediately
            System.out.println("No order specified");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Machine-agent " + getAID().getName() + " terminating.");
    }

    private class RequestPerformer extends Behaviour {
        private AID currentDish;
        private String currentDishName;
        private MessageTemplate messageTemplate;
        private int step = 0;
        private long start;
        private static final String CONVERSATION_ID = "order-trade";

        @Override
        public void action() {
            switch (step) {
                case 0:
                    ACLMessage cfpMessage = new ACLMessage(ACLMessage.CFP);
                    for (AID agent : OrderAgents) {
                        cfpMessage.addReceiver(agent);
                    }

                    String targetDishesStr = "";
//                    for (int i = 0; i < targetDishes.size(); i++) {
//                        targetDishesStr += targetDishes.get(i).getName() + ";";
//                    }
                    //cfpMessage.setContent(targetDishesStr);
                    cfpMessage.setContent(targetDish.getName());
                    cfpMessage.setContent(targetDishesStr);
                    cfpMessage.setConversationId(CONVERSATION_ID);
                    cfpMessage.setReplyWith("cfp" + System.currentTimeMillis());

                    messageTemplate = MessageTemplate.and(
                            MessageTemplate.MatchConversationId(CONVERSATION_ID),
                            MessageTemplate.MatchInReplyTo(cfpMessage.getReplyWith()));

                    step = 1;
                    break;
                case 1:
                    ACLMessage reply = myAgent.receive(messageTemplate);
                    if (reply != null) {
                        currentDish = reply.getSender();
                        currentDishName = reply.getContent();
                    } else {
                        block();
                    }

                    step = 2;

                    break;
                case 2:
                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

                    order.addReceiver(currentDish);
                    order.setContent(currentDishName);
                    order.setConversationId(CONVERSATION_ID);
                    start = System.currentTimeMillis();
                    order.setReplyWith("Cooking started");

                    myAgent.send(order);

                    messageTemplate = MessageTemplate.and(
                            MessageTemplate.MatchConversationId(CONVERSATION_ID),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith())
                    );

                    step = 3;
                    break;
                case 3:
                    try {
                        wait(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    reply = myAgent.receive(messageTemplate);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            System.out.println(currentDishName + " successfully cooked in " + (start - System.currentTimeMillis()) * 1000 + " seconds");
                            myAgent.doDelete();
                        }
                        step = 4;
                    } else {
                        block();
                    }

                    break;
            }
        }

        @Override
        public boolean done() {
            return ((step == 2 && currentDish == null) || step == 4);
        }
    }
}
