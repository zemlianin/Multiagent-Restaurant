package org.example.behaviour;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import org.example.models.Person;
import org.example.util.ACLMessageUtil;

public class ReceiveMessageBehaviour extends Behaviour {
    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            System.out.println("Received: " + ACLMessageUtil.getContent(msg, Person.class));
        }
        else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}