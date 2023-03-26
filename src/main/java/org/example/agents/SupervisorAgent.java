package org.example.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class SupervisorAgent extends Agent {

    private AID storageAgent; // Агент склада
    private int orderNumber = 1; // Номер заказа

    protected void setup() {
        System.out.println("Supervisor agent " + getAID().getName() + " is ready.");
        // Получение ссылки на агента склада
        storageAgent = new AID("storage", AID.ISLOCALNAME);
        addBehaviour(new OrderBehaviour());
    }

    private class OrderBehaviour extends CyclicBehaviour {
        public void action() {
            // Ожидание запроса на создание нового заказа
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = receive(mt);
            if (msg != null) {
                // Создание нового агента заказа
                String orderName = "order" + orderNumber++;
                try {
                    AgentController orderAgent = getContainerController().createNewAgent(orderName, OrderAgentBroke.class.getName(), null);
                    orderAgent.start();
                    // Отправка сообщения агенту склада с запросом на резервирование продуктов
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    request.addReceiver(storageAgent);
                    request.setContent(msg.getContent());
                    request.setConversationId(orderName);
                    send(request);
                    // Ожидание подтверждения резервирования от агента склада
                    mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                            MessageTemplate.MatchConversationId(orderName));
                    ACLMessage reply = receive(mt);
                    if (reply != null) {
                        // Отправка сообщения агенту заказа с информацией о резервировании продуктов
                        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
                        inform.addReceiver(new AID(orderName, AID.ISLOCALNAME));
                        inform.setContent(reply.getContent());
                        send(inform);
                    } else {
                        System.out.println("Reservation failed for " + orderName);
                    }
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }
    }

    protected void takeDown() {
        System.out.println("Supervisor agent " + getAID().getName() + " is terminating.");
    }
}

