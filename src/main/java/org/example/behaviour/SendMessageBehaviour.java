package org.example.behaviour;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import org.example.models.Person;
import org.example.util.JsonMessage;

import java.util.ArrayList;
import java.util.List;

public class SendMessageBehaviour<T> extends Behaviour {

    private final T message;
    private final AID[] recipients;

    public SendMessageBehaviour(AID[] recipients, T message) {
        this.recipients = recipients;
        this.message = message;
    }

    @Override
    public void action() {
        JsonMessage cfp = new JsonMessage(ACLMessage.CFP);
        for (AID recipient : recipients) {
            cfp.addReceiver(recipient);
        }
        cfp.setContent(message);
        myAgent.send(cfp);
    }

    @Override
    public boolean done() {
        return true;
    }
}
