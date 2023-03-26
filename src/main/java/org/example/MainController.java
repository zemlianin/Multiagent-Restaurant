package org.example;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.example.agents.VisitorAgent;
import org.example.models.Visitor;
import org.reflections.Reflections;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

class MainController {

    private final ContainerController containerController;
    private final Visitor[] visitors;

    public MainController(Visitor[] visitors) {
        this.visitors = visitors;
        final Runtime rt = Runtime.instance();
        final Profile p = new ProfileImpl();

        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "8080");
        p.setParameter(Profile.GUI, "true");

        containerController = rt.createMainContainer(p);
    }

    void initAgents() {
        initAgents(MainController.class.getPackageName());
    }

    void initAgents(String basePackage) {
        final Reflections reflections = new Reflections(basePackage);

        final Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(JadeAgent.class);
        try {
            for (Class<?> clazz : allClasses) {
                if (Agent.class.isAssignableFrom(clazz)) {
                    configureAgent(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureAgent(Class<?> clazz) throws StaleProxyException {
        final JadeAgent jadeAgent = clazz.getAnnotation(JadeAgent.class);

        if (jadeAgent.number() <= 0) {
            throw new IllegalStateException(MessageFormat.format(
                    "Number of agent {0} is less then 1. Real number is {1}",
                    clazz.getName(),
                    jadeAgent.number()
            ));
        }

        final String agentName =
                !Objects.equals(jadeAgent.value(), "")
                        ? jadeAgent.value()
                        : clazz.getSimpleName();
        ArrayList<Object> arg = new ArrayList<>();
        if (jadeAgent.number() == 1) {
            if (clazz == VisitorAgent.class) {
                arg.add(visitors[0]);
            }
            createAgent(clazz, agentName, arg.toArray()).start();
        } else {
            for (int i = 0; i < jadeAgent.number(); ++i) {
                if (clazz == VisitorAgent.class) {
                    arg.add(visitors[i]);
                }
                createAgent(
                        clazz,
                        MessageFormat.format(
                                "{0}{1}",
                                agentName,
                                i
                        ), arg.toArray()).start();

            }
        }
    }

    private AgentController createAgent(Class<?> clazz, String agentName, Object[] arg) throws StaleProxyException {
        return containerController.createNewAgent(
                agentName,
                clazz.getName(),
                arg);
    }
}
