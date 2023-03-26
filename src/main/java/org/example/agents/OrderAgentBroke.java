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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderAgentBroke extends Agent {
    private Map<String, Integer> orderList;
    private int estimatedWaitTime;

    @Override
    protected void setup() {
        ArrayList<AID> foodAndDrinkAgents = new ArrayList<>();
        orderList = new HashMap<>();
        estimatedWaitTime = 0;

        // Регистрация агента в желтых страницах
        registerInYellowPages();

        // Создание циклического поведения для обработки сообщений
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Определение шаблона сообщения для получения сообщений от управляющего агента
                MessageTemplate mt = MessageTemplate.MatchSender(new AID("supervisor", AID.ISLOCALNAME));
                ACLMessage msg = myAgent.receive(mt);

                if (msg != null) {
                    switch (msg.getPerformative()) {
                        case ACLMessage.REQUEST:
                            // Получение запроса на информацию о времени ожидания
                            if (msg.getContent().equals("waitTime")) {
                                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                                reply.addReceiver(msg.getSender());
                                reply.setContent(String.valueOf(estimatedWaitTime));
                                send(reply);
                            }
                            break;
                        case ACLMessage.INFORM:
                            // Получение информации о времени ожидания готовности блюд / напитков
                            if (msg.getContent().contains("estimatedTime")) {
                                String[] content = msg.getContent().split(":");
                                String product = content[0];
                                int time = Integer.parseInt(content[1]);
                                estimatedWaitTime += time;
                                System.out.println("Order agent received estimated time of " + time + " for " + product);
                            }
                            break;
                    }
                } else {
                    // Если сообщение не получено, то ждем
                    block();
                }
            }
        });
    }

    // Регистрация агента в желтых страницах
    private void registerInYellowPages() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Order");
        sd.setName(getLocalName() + "-order-agent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    // Отправка запроса на информацию о времени ожидания готовности блюд / напитков
    private void requestEstimatedTime(String productName) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("process", AID.ISLOCALNAME));
        msg.setContent(productName);
        send(msg);
    }

    // Резервирование необходимых ресурсов для выполнения заказа
    private void reserveResources() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("storage", AID.ISLOCALNAME));
        msg.setContent(orderList.toString());
        send(msg);
    }

    // Отмена резерва
    private void cancelReservation(String productName, int quantity) {
        ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
        msg.addReceiver(new AID("storage", AID.ISLOCALNAME));
        Map<String, Integer> cancelMap = new HashMap<>();
        cancelMap.put(productName, quantity);
        msg.setContent(cancelMap.toString());
        send(msg);
    }

    // Обработка ответа от агентов продуктов о результате резервирования
    private void handleReservationResponse(ACLMessage msg) {
        switch (msg.getPerformative()) {
            case ACLMessage.CONFIRM:
                System.out.println("Order agent received confirmation from " + msg.getSender().getLocalName());
                break;
            case ACLMessage.DISCONFIRM:
                System.out.println("Order agent received disconfirmation from " + msg.getSender().getLocalName());
                break;
        }
    }

    // Продолжение процесса выполнения заказа
    private void continueOrder() {
        if (!orderList.isEmpty()) {
            // Определение продукта, который нужно заказать
            String productName = "";
            int productQuantity = 0;
            for (Map.Entry<String, Integer> entry : orderList.entrySet()) {
                if (entry.getValue() > 0) {
                    productName = entry.getKey();
                    productQuantity = entry.getValue();
                    break;
                }
            }
            if (!productName.isEmpty() && productQuantity > 0) {
                // Заказ продукта
                System.out.println("Order agent is ordering " + productQuantity + " " + productName);
                requestEstimatedTime(productName);
                reserveResources();
            } else {
                // Заказ выполнен
                System.out.println("Order agent has completed the order");
            }
        } else {
            // Заказ выполнен
            System.out.println("Order agent has completed the order");
        }
    }
}
