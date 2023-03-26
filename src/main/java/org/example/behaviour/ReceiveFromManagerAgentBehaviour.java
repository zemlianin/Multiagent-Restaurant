package org.example.behaviour;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.agents.ManagerAgent;
import org.example.agents.OrderAgent;
import org.example.util.ACLMessageUtil;
import org.example.util.JsonMessage;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReceiveFromManagerAgentBehaviour<T> extends CyclicBehaviour {

    final Class<T> typeParameterClass;
    Object o = new Object();

    public ReceiveFromManagerAgentBehaviour(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public void action() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            AID[] recipients = getOrderAgent();
            var order = ACLMessageUtil.getContent(msg, typeParameterClass);
            JsonMessage cfp = new JsonMessage(ACLMessage.CFP);
            for (AID recipient : recipients) {
                cfp.addReceiver(recipient);
            }
            cfp.setContent(order);
            myAgent.send(cfp);
            System.out.println("Received: " + ACLMessageUtil.getContent(msg, typeParameterClass));
        } else {
            block();
        }
    }

    public AID[] getOrderAgent() {
        ArrayList<AID> orderAgens = new ArrayList<>();
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();


        serviceDescription.setType(OrderAgent.AGENT_TYPE);
        template.addServices(serviceDescription);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            for (DFAgentDescription agentDescription : result) {
                orderAgens.add(agentDescription.getName());
            }
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
        return orderAgens.toArray(new AID[0]);
    }
}