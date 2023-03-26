package org.example.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class DishAgentBroke extends Agent {

    private List<String> processAgents; // список агентов процесса
    private List<String> operationAgents; // список агентов операций
    private List<String> productAgents; // список агентов продуктов

    @Override
    protected void setup() {
        processAgents = new ArrayList<>();
        operationAgents = new ArrayList<>();
        productAgents = new ArrayList<>();

        // Добавляем поведение для обработки сообщений
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    // Обработка сообщений
                    switch (msg.getPerformative()) {
                        case ACLMessage.INFORM:
                            // Получение списков агентов процесса, операций и продуктов
                            String content = msg.getContent();
                            String[] agents = content.split(",");

                            // Сохраняем списки агентов
                            processAgents = new ArrayList<>();
                            operationAgents = new ArrayList<>();
                            productAgents = new ArrayList<>();

                            for (String agent : agents) {
                                if (agent.startsWith("Process")) {
                                    processAgents.add(agent);
                                } else if (agent.startsWith("Operation")) {
                                    operationAgents.add(agent);
                                } else if (agent.startsWith("Product")) {
                                    productAgents.add(agent);
                                }
                            }

                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            // Заказ на приготовление блюда / напитка принят
                            System.out.println(getLocalName() + ": Заказ принят. Приступаем к приготовлению.");

                            // Можно начинать приготовление блюда / напитка
                            // ...

                            // Отправляем сообщение об окончании приготовления
                            ACLMessage reply = msg.createReply();
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("Готово");
                            send(reply);

                            // Завершаем работу агента
                            doDelete();
                            break;
                    }
                } else {
                    // Если сообщений нет, ждем следующее
                    block();
                }
            }
        });
    }
}
