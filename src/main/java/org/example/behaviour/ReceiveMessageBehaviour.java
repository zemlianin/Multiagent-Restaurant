package org.example.behaviour;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import org.example.util.ACLMessageUtil;

public class ReceiveMessageBehaviour<T> extends Behaviour {
    final Class<T> typeParameterClass;

    public ReceiveMessageBehaviour(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            System.out.println("Received: " + ACLMessageUtil.getContent(msg, typeParameterClass));
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