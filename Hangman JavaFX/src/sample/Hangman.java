package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Hangman extends Application {

    //Pane and labels
    BorderPane borderPane = new BorderPane();
    TextField guessField = new TextField();
    Label wrongLettersLabel = new Label("Väärät arvaukset:\n");
    Label hangman = new Label();
    Label wordLabel = new Label();
    Label msg = new Label();


    //Gives a random word from file sanat.txt
    String word = giveWord();
    int wordLength = word.length();


    int lives = 5;             //Amount of lives
    int correct = 0;            //Amount of correct guesses
    int oldCorrect = 0;     //Memoryplace for the previous amount of correct guesses


    //Max. word length 12. Correct letter turns 0 to 1 in the array, so that it prints correctly
    int[] guesses = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    ArrayList<Character> wrongLetters = new ArrayList<>();
    ArrayList<Character> correctLetters = new ArrayList<>();


    @Override
    public void start(Stage stage) {
        //Add the labels to the pane
        borderPane.setPrefSize(850, 500);
        HBox hboxCenter = new HBox();
        hboxCenter.setSpacing(100);
        hboxCenter.getChildren().addAll(wordLabel, hangman);
        borderPane.setCenter(hboxCenter);
        HBox hboxBottom = new HBox();
        hboxBottom.setSpacing(150);
        hboxBottom.setPadding(new Insets(15, 12, 15, 12));
        hboxBottom.getChildren().addAll(wrongLettersLabel, guessField);
        borderPane.setBottom(hboxBottom);
        borderPane.setTop(msg);

        //Style the labels and pane
        borderPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        hboxBottom.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        wordLabel.setFont(new Font("Arial", 40));
        hangman.setFont(new Font("Arial", 40));
        wrongLettersLabel.setFont(new Font("Arial", 22));
        msg.setFont(new Font("Arial", 22));

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();

        //Prints the word first time as lines without guessed letters
        for (int i = 0; i < wordLength; i++) {
            wordLabel.setText(wordLabel.getText() + "-   ");
        }

        //When inputting a letter this event happens
        guessField.setOnAction((event) -> {
            wordLabel.setText("");
            msg.setText("");
            char letter;

            //Input can't be empty or a digit
            if(guessField.getText().isEmpty()){
                msg.setText("Syötä kirjain!");
                guessField.clear();

                for (int i = 0; i < wordLength; i++) {
                    if (guesses[i] == 1) {
                        
                        wordLabel.setText(wordLabel.getText() + word.charAt(i) + "   ");
                    } else {
                       
                        wordLabel.setText(wordLabel.getText() + "-   ");
                    }
                }
            }else if (Character.isDigit(guessField.getText().charAt(0))) {
                    msg.setText("Syötä kirjain!");
                    guessField.clear();

                    for (int i = 0; i < wordLength; i++) {
                        if (guesses[i] == 1) {
                            
                            wordLabel.setText(wordLabel.getText() + word.charAt(i) + "   ");
                        } else {
                            
                            wordLabel.setText(wordLabel.getText() + "-   ");
                        }
                    }
                    //Go here, when the input is correct
                } else {
                    letter = guessField.getText().charAt(0);

                    //If the there are still letters to guess
                    if (correct < wordLength) {
                        //Store the old amount of correct letters
                        oldCorrect = correct;
                        //Check if the letters is already inputted
                        if (wrongLetters.contains(letter) || correctLetters.contains(letter)) {
                            msg.setText("Kirjain jo syötetty");

                        } else {

                            //Go through the word letter by letter
                            for (int i = 0; i < wordLength; i++) {
                                //If the guessed letter is found in the word. Change that place to 1 in the array, and
                                //increase the amount of correct guesses
                                if (letter == word.charAt(i)) {
                                    guesses[i] = 1;
                                    correct++;
                                }
                            }

                            //If the amount of correct guesses didn't increase in the previous loop, then
                            //the guess was incorrect
                            if (oldCorrect == correct) {
                                wrongLettersLabel.setText(wrongLettersLabel.getText() + " " + letter + " ");
                                wrongLetters.add(letter);
                                lives--;
                                msg.setText("Väärin arvattu. Elämiä jäljellä: " + lives);
                            } else {
                                msg.setText("Oikein arvattu!");
                                correctLetters.add(letter);
                            }
                        }

                        //Update the guess word with correctly guessed letter
                        for (int i = 0; i < wordLength; i++) {
                            if (guesses[i] == 1) {
                                //Print the letter if guessed correctly
                                wordLabel.setText(wordLabel.getText() + word.charAt(i) + "   ");
                            } else {
                                //Print a line
                                wordLabel.setText(wordLabel.getText() + "-   ");
                            }
                        }

                        Image image = new Image(getClass().getResourceAsStream(hangmanPicture(lives)));
                        hangman.setGraphic(new ImageView(image));
                        guessField.clear();

                    }
                }

            //If ran out of lives, then show the game over-screen
            if (lives == 0) {
                BorderPane losepane = new BorderPane();
                losepane.setPrefSize(850,500);
                Label losermessage = new Label("Hävisit pelin!\nOikea vastaus oli: "+word);
                Image image = new Image(getClass().getResourceAsStream("pics/0life.jpg"));
                losermessage.setGraphic(new ImageView(image));
                losermessage.setFont(new Font("Arial", 40));
                losepane.setCenter(losermessage);
                Scene loserscene = new Scene(losepane);
                Button newgame = new Button("Uusi peli");
                Button exit = new Button("Lopeta");
                BorderPane.setMargin(newgame,(new Insets(15, 12, 15, 12)));
                losepane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                VBox buttons = new VBox();
                buttons.setPrefWidth(80);
                newgame.setMinWidth(buttons.getPrefWidth());
                exit.setMinWidth(buttons.getPrefWidth());
                buttons.getChildren().addAll(newgame,exit);
                losepane.setRight(buttons);

                exit.setOnAction((e2) -> Platform.exit());

                newgame.setOnAction((e) -> {
                    Hangman restart = new Hangman();
                    restart.start(stage);
                });

                stage.setScene(loserscene);

                //If there are more or equal amount of correct letters than the length of the word. You win the game.
            } else if (correct >= wordLength) {
                BorderPane winpane = new BorderPane();
                winpane.setPrefSize(850,500);
                Label winnermessage = new Label("Voitit pelin!\nOikea vastaus oli: "+word);
                Image image = new Image(getClass().getResourceAsStream("pics/win.jpg"));
                winnermessage.setGraphic(new ImageView(image));
                winnermessage.setFont(new Font("Arial", 24));
                winpane.setCenter(winnermessage);
                Scene winnerscene = new Scene(winpane);
                Button newgame = new Button("Uusi peli");
                Button exit = new Button("Lopeta");
                BorderPane.setMargin(newgame,(new Insets(15, 12, 15, 12)));
                winpane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                VBox buttons = new VBox();
                buttons.setPrefWidth(80);
                newgame.setMinWidth(buttons.getPrefWidth());
                exit.setMinWidth(buttons.getPrefWidth());
                buttons.getChildren().addAll(newgame,exit);
                winpane.setRight(buttons);

                exit.setOnAction((e2) -> Platform.exit());

                newgame.setOnAction((e) -> {
                    Hangman restart = new Hangman();
                    restart.start(stage);
                });

                stage.setScene(winnerscene);
            }


        });


    }



    //Give a random word from the set of words
    public static String giveWord() {
        ArrayList<String> sanat = giveWords();
        Random random = new Random();
        String sana = sanat.get(random.nextInt(sanat.size()));
        return sana;
    }

    //Return a set of words from the file sanat.txt
    public static ArrayList<String> giveWords() {
        ArrayList<String> sanat = new ArrayList<>();

        // luodaan lukija tiedoston lukemista varten
        try (Scanner tiedostonLukija = new Scanner(new File("words.txt"))) {

            // luetaan kaikki tiedoston rivit
            while (tiedostonLukija.hasNextLine()) {
                sanat.add(tiedostonLukija.nextLine());
            }
        } catch (Exception e) {
            System.out.println("Virhe: " + e.getMessage());
        }
        return sanat;
    }

    //Return the correct file to show the correct picture
    public static String hangmanPicture(int lives) {
        if (lives == 4) {
            return "pics/4life.jpg";
        } else if (lives == 3) {
            return "pics/3life.jpg";
        } else if (lives == 2) {
            return "pics/2life.jpg";
        } else if (lives == 1) {
            return "pics/1life.jpg";
        }
        return "";
    }

    public static void main(String[] args) {
        launch(Hangman.class);
    }
}
