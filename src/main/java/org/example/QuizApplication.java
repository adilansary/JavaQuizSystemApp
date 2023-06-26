package org.example;

import java.util.Collections;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class QuizApplication {
    private static final String USERS_FILE = "src/main/java/org/example/users.json";
    private static final String QUIZ_FILE = "src/main/java/org/example/quiz.json";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        JSONParser jsonParser = new JSONParser();

        System.out.println("System:> Enter your username");
        String username = scanner.nextLine();

        System.out.println("System:> Enter password");
        String password = scanner.nextLine();

        try {
            JSONArray users = (JSONArray) jsonParser.parse(new FileReader(USERS_FILE));
            JSONObject user = findUser(users, username, password);

            if (user != null) {
                String role = (String) user.get("role");
                if (role.equals("admin")) {
                    System.out.println("System:> Welcome admin! Please create new questions in the question bank.");
                    adminFunctionality(scanner, jsonParser);
                } else if (role.equals("student")) {
                    System.out.println("System:> Welcome " + username + " to the quiz! We will throw you 10 questions. Each MCQ mark is 1 and no negative marking. Are you ready? Press 's' for start.");
                    String start = scanner.nextLine();
                    if (start.equalsIgnoreCase("s")) {
                        studentFunctionality(scanner, jsonParser);
                    }
                }
            } else {
                System.out.println("System:> Invalid username or password.");
            }
        } catch (Exception e) {
            System.out.println("System:> An error occurred: " + e.getMessage());
        }
    }

    private static JSONObject findUser(JSONArray users, String username, String password) {
        for (Object obj : users) {
            JSONObject user = (JSONObject) obj;
            String userUsername = (String) user.get("username");
            String userPassword = (String) user.get("password");
            if (userUsername.equals(username) && userPassword.equals(password)) {
                return user;
            }
        }
        return null;
    }

    private static void adminFunctionality(Scanner scanner, JSONParser jsonParser) {
        JSONArray quiz = readQuizFile(jsonParser);
        boolean addMoreQuestions = true;

        while (addMoreQuestions) {
            System.out.println("System:> Enter the question:");
            String question = scanner.nextLine();

            System.out.println("System:> Enter option 1:");
            String option1 = scanner.nextLine();

            System.out.println("System:> Enter option 2:");
            String option2 = scanner.nextLine();

            System.out.println("System:> Enter option 3:");
            String option3 = scanner.nextLine();

            System.out.println("System:> Enter option 4:");
            String option4 = scanner.nextLine();

            System.out.println("System:> Enter the answer key (1-4):");
            int answerKey = Integer.parseInt(scanner.nextLine());

            JSONObject newQuestion = new JSONObject();
            newQuestion.put("question", question);
            newQuestion.put("option 1", option1);
            newQuestion.put("option 2", option2);
            newQuestion.put("option 3", option3);
            newQuestion.put("option 4", option4);
            newQuestion.put("answerkey", answerKey);

            quiz.add(newQuestion);

            System.out.println("System:> Question added successfully!");

            System.out.println("System:> Do you want to add more questions? (y/n)");
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("n")) {
                addMoreQuestions = false;
            }
        }

        saveQuizFile(quiz);
        System.out.println("System:> Questions saved successfully!");
    }

    private static JSONArray readQuizFile(JSONParser jsonParser) {
        try {
            JSONArray quiz = (JSONArray) jsonParser.parse(new FileReader(QUIZ_FILE));
            return quiz;
        } catch (Exception e) {
            System.out.println("System:> An error occurred while reading quiz file: " + e.getMessage());
        }
        return new JSONArray();
    }

    private static void saveQuizFile(JSONArray quiz) {
        try (FileWriter fileWriter = new FileWriter(QUIZ_FILE)) {
            fileWriter.write(quiz.toJSONString());
        } catch (Exception e) {
            System.out.println("System:> An error occurred while saving quiz file: " + e.getMessage());
        }
    }

    private static void studentFunctionality(Scanner scanner, JSONParser jsonParser) {
        JSONArray quiz = readQuizFile(jsonParser);
        int score = 0;
        Collections.shuffle(quiz);
        int numQuestions = Math.min(quiz.size(), 10);
        JSONArray quizSubset = new JSONArray();

        for (int i = 0; i < numQuestions; i++) {
            quizSubset.add(quiz.get(i));
        }

        for (Object obj : quizSubset) {
            JSONObject question = (JSONObject) obj;

            String questionText = (String) question.get("question");
            String option1 = (String) question.get("option 1");
            String option2 = (String) question.get("option 2");
            String option3 = (String) question.get("option 3");
            String option4 = (String) question.get("option 4");
            int answerKey = ((Long) question.get("answerkey")).intValue();

            System.out.println("Question: " + questionText);
            System.out.println("Options:");
            System.out.println("1. " + option1);
            System.out.println("2. " + option2);
            System.out.println("3. " + option3);
            System.out.println("4. " + option4);

            System.out.println("Enter your answer (1/2/3/4):");
            int userAnswer = Integer.parseInt(scanner.nextLine());

            if (userAnswer == answerKey) {
                score++;
                System.out.println("Correct answer!");
            } else {
                System.out.println("Wrong answer!");
            }
        }

        System.out.println("Quiz completed! Your final score: " + score + "/" + numQuestions);
    }

}


