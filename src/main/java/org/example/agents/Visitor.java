package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.example.JadeAgent;
import org.example.behaviour.SendMessageBehaviour;
import org.example.models.Dish;
import org.example.models.Order;
import org.example.models.Person;

import java.util.ArrayList;
import java.util.List;

@JadeAgent(number = 1)
public class Visitor extends Agent {
    private List<AID> managerAgents = new ArrayList<>();

    @Override
    protected void setup() {
        System.out.println("Я посетитель и я зашел");
        makeOrder();
    }

    protected void makeOrder() {
        addBehaviour(new TickerBehaviour(this, 2000) {
            protected void onTick() {
                System.out.println(getAID().getName() + " Сделал заказ");


                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();


                serviceDescription.setType(Manager.AGENT_TYPE);
                template.addServices(serviceDescription);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    System.out.print(result.length);
                    for (DFAgentDescription agentDescription : result) {
                        managerAgents.add(agentDescription.getName());
                    }
                } catch (FIPAException ex) {
                    ex.printStackTrace();
                }

                myAgent.addBehaviour(new SendMessageBehaviour(managerAgents.toArray(new AID[0]),
                        new Order(new Dish[]{new Dish("Chicken", 200), new Dish("Patato", 100)}
                        )));


            }
        });
    }
}
