package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Visitor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
/*
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Visitor> v = new ArrayList<>();
        var ar = new ArrayList<String>();
        ar.add("potato");
        ar.add("chicken");
        v.add(new Visitor("Ivan", ar));
        v.add(new Visitor("Anton", ar));
        var t = objectMapper.writeValueAsString(v);
        try (FileWriter writer = new FileWriter("visitors.json", false)) {
            writer.write(t);
            // writer.flush();
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }*/
        Visitor[] visitors = getVisitors();
        MainController mainController = new MainController(visitors);
        mainController.initAgents("org.example.agents");
    }

    public static Visitor[] getVisitors() {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = "";
        Visitor[] visitors = new Visitor[0];
        try (FileReader reader = new FileReader("visitors.json")) {
            // читаем посимвольно
            int c;
            while ((c = reader.read()) != -1) {
                content += (char) c;
            }
            visitors = objectMapper.readValue(content, Visitor[].class);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return visitors;
    }
}