package com.github.dedinc.mindvault.core;

import com.github.dedinc.mindvault.core.objects.Card;
import com.github.dedinc.mindvault.core.objects.State;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    public static void saveSession(Session session, String filename) {
        JSONObject json = new JSONObject();
        json.put("typeSpeed", session.getTypeSpeed());
        json.put("perSessionCards", session.getPerSessionCards());

        JSONObject cardsJson = new JSONObject();
        for (Map.Entry<State, List<Card>> entry : session.getCategories().entrySet()) {
            JSONArray cardsArray = new JSONArray();
            for (Card card : entry.getValue()) {
                JSONObject cardJson = new JSONObject();
                cardJson.put("question", card.getQuestion());
                cardJson.put("answer", card.getAnswer());
                cardJson.put("learnDate", card.getLearnDate());
                cardJson.put("reviseDates", new JSONArray(card.getReviseDates()));
                cardsArray.put(cardJson);
            }
            cardsJson.put(entry.getKey().toString(), cardsArray);
        }
        json.put("cards", cardsJson);

        JSONArray gradesJson = new JSONArray(session.getGrades());
        json.put("grades", gradesJson);

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json.toString(2));
            System.out.println("Session saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving session: " + e.getMessage());
        }
    }

    public static Session loadSession(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            JSONObject json = new JSONObject(new JSONTokener(reader));

            int typeSpeed = json.getInt("typeSpeed");
            int perSessionCards = json.getInt("perSessionCards");

            Map<State, List<Card>> cards = new HashMap<>();
            JSONObject cardsJson = json.getJSONObject("cards");
            for (String stateStr : cardsJson.keySet()) {
                State state = State.valueOf(stateStr);
                List<Card> cardList = new ArrayList<>();
                JSONArray cardsArray = cardsJson.getJSONArray(stateStr);
                for (int i = 0; i < cardsArray.length(); i++) {
                    JSONObject cardJson = cardsArray.getJSONObject(i);
                    String question = cardJson.getString("question");
                    String answer = cardJson.getString("answer");
                    long learnDate = cardJson.getLong("learnDate");
                    JSONArray reviseDatesJson = cardJson.getJSONArray("reviseDates");
                    long[] reviseDates = new long[reviseDatesJson.length()];
                    for (int j = 0; j < reviseDatesJson.length(); j++) {
                        reviseDates[j] = reviseDatesJson.getLong(j);
                    }
                    Card card = new Card(question, answer, learnDate, reviseDates);
                    cardList.add(card);
                }
                cards.put(state, cardList);
            }

            List<Double> grades = new ArrayList<>();
            JSONArray gradesJson = json.getJSONArray("grades");
            for (int i = 0; i < gradesJson.length(); i++) {
                grades.add(gradesJson.getDouble(i));
            }

            Session session = new Session();
            session.setTypeSpeed(typeSpeed);
            session.setCards(cards);
            session.setGrades(grades);
            session.setPerSessionCards(perSessionCards);

            return session;
        } catch (IOException e) {
            System.out.println("Error loading session: " + e.getMessage());
            return null;
        }
    }
}