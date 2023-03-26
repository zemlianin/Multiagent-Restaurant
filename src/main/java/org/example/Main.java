package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Dish;
import org.example.models.Menu;
import org.example.models.Visitor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
/*        ObjectMapper objectMapper = new ObjectMapper();
        var t = new ArrayList<Dish>();
        t.add(new Dish("chicken", 200, 100));
        t.add(new Dish("potato", 200, 100));
        t.add(new Dish("tomato", 100, 10));
        t.add(new Dish("meet", 500, 400));
        t.add(new Dish("water", 10, 10));
        t.add(new Dish("coffee", 200, 200));
        var v = objectMapper.writeValueAsString(new Menu(t));
        try (FileWriter writer = new FileWriter("data/menu.json", false)) {
            writer.write(v);
            // writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }*/
        Visitor[] visitors = getVisitors();
        Menu menu = getMenu();
        MainController mainController = new MainController(visitors, menu);
        mainController.initAgents("org.example.agents");
    }

    public static Menu getMenu() {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = "";
        Menu menu = new Menu();
        try (FileReader reader = new FileReader("data/menu.json")) {
            // читаем посимвольно
            int c;
            while ((c = reader.read()) != -1) {
                content += (char) c;
            }
            menu = objectMapper.readValue(content, Menu.class);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return menu;
    }

    public static Visitor[] getVisitors() {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = "";
        Visitor[] visitors = new Visitor[0];
        try (FileReader reader = new FileReader("data/visitors.json")) {
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