package org.example.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.example.JadeAgent;
import org.example.behaviour.ReceiveMessageBehaviour;
import org.example.models.Dish;
import org.example.models.Menu;
import org.example.models.Order;
import org.example.models.Visitor;

import java.util.ArrayList;

@JadeAgent("Manager")
public class ManagerAgent extends Agent {
    public static final String AGENT_TYPE = "Manager";
    public static Menu menu;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            menu = (Menu) args[0];

            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Manager");
            sd.setName("Manager");
            dfd.addServices(sd);
            try {
                DFService.register(this, dfd);
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

            addBehaviour(new ReceiveMessageBehaviour(Order.class));
        } else {
            System.out.println("No wishes title specified");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Print out a dismissal message
        System.out.println(getAID().getName() + " terminating");
    }

}