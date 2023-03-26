package org.example.behaviour;


import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import org.example.util.JsonMessage;


public class SendOrderToManagerBehaviour<T> extends Behaviour {
    private int step = 0;
    private final T message;
    private final AID[] recipients;

    public SendOrderToManagerBehaviour(AID[] recipients, T message) {
        this.recipients = recipients;
        this.message = message;
    }

    @Override
    public void action() {
        while (!done()) {
            switch (step) {
                case 0:
                    JsonMessage cfp = new JsonMessage(ACLMessage.CFP);
                    for (AID recipient : recipients) {
                        cfp.addReceiver(recipient);
                    }
                    cfp.setContent(message);
                    myAgent.send(cfp);
                    step = 1;
                    break;
                case 1:
                    System.out.println("Жду заказ - " + myAgent.getName());
                    step = 2;
                    break;
                case 2:
                    System.out.println("Я пошел домой - " + myAgent.getName());
                    myAgent.doDelete();
                    step = 3;
                    break;
            }
        }
    }

    @Override
    public boolean done() {
        return step == 3;
    }
}