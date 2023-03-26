package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.example.JadeAgent;
import org.example.behaviour.SendMessageBehaviour;
import org.example.behaviour.SendOrderToManagerBehaviour;
import org.example.models.Dish;
import org.example.models.Menu;
import org.example.models.Order;
import org.example.models.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@JadeAgent(number = 2)
public class VisitorAgent extends Agent {

    private List<String> wishes = new ArrayList<>();
    private List<AID> managerAgents = new ArrayList<>();

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            Visitor visitor = (Visitor) args[0];
            wishes = visitor.wishes;
            System.out.println("Я посетитель и я зашел");
            makeOrder();
        } else {
            System.out.println("No wishes title specified");
            doDelete();
        }
    }

    protected Dish[] selectDishes() {
        var menu = ManagerAgent.menu;

        ArrayList<Dish> dishes = new ArrayList<>();
        for (var wish : wishes) {
            var wantDish = menu.getDishes().stream().filter(p -> p.getName().equals(wish)).findAny();
            if (wantDish.isPresent()) {
                dishes.add(wantDish.get());
            } else {
                System.out.print("Блюда с названием " + wish + " нет в меню \n");
            }
        }
        return dishes.toArray(new Dish[0]);
    }

    protected void makeOrder() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getAID().getName() + " Сделал заказ");

                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();


                serviceDescription.setType(ManagerAgent.AGENT_TYPE);
                template.addServices(serviceDescription);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    for (DFAgentDescription agentDescription : result) {
                        managerAgents.add(agentDescription.getName());
                    }
                } catch (FIPAException ex) {
                    ex.printStackTrace();
                }
                myAgent.addBehaviour(new SendOrderToManagerBehaviour<Order>(managerAgents.toArray(new AID[0]),
                        new Order(selectDishes()
                        )));
            }
        });
    }
}
