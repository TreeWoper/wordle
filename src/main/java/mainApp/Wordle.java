package mainApp;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.util.*;

public class Wordle extends Application {

    private Block[][] squares = new Block[6][5];
    private Button[][] keyboard = new Button[3][];
    private final int size = 50;
    private BoardData bd;
    private Label message;
    private String[][] keys = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"Enter", "Z", "X", "C", "V", "B", "N", "M", "⌫"}
    };
    private Button settings;
    private final double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth()*.45*.8;
    private final double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight()*.9*.9;
    private Stage mainStage;
    private VBox keyboardBox;
    private GridPane squaresGrid;
    private MediaPlayer mp;


    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        bd = new BoardData();
        message = new Label("");
        mainStage = primaryStage;
        mainStage.setScene(gameWindow());
        mainStage.setTitle("Wordle!");
        mainStage.show();
        Media sound = new Media(getClass().getResource("/backMusic.mp3").toString());
        mp = new MediaPlayer(sound);
        mp.setOnEndOfMedia(() -> mp.seek(javafx.util.Duration.ZERO));
        mp.play();
    }

    private Scene settingsWindow(){//makes the scene for the game window
        VBox root = new VBox();
        HBox top = new HBox();
        VBox middle = new VBox();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(5), Insets.EMPTY)));
        root.setAlignment(Pos.TOP_CENTER);

        Text settingsMessage = new Text("Settings");
        settingsMessage.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.8)); // Set font and size
        settingsMessage.setFill(Color.WHITE);
        top.getChildren().add(settingsMessage);

        settings.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
        final Image[] image = {new Image(getClass().getResource("/unpause.png").toString())};
        final ImageView[] imageView = {new ImageView(image[0])};
        imageView[0].setFitWidth(25);
        imageView[0].setFitHeight(25);
        settings.setGraphic(imageView[0]);
        top.getChildren().add(settings);

        top.setSpacing(60);
        top.setAlignment(Pos.CENTER);
        root.getChildren().add(top);

        Text volumeLable = new Text("Volume: ");
        volumeLable.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.4)); // Set font and size
        Slider volumeSlider = new Slider(0.0, 3.0, 0.5);
        volumeSlider.setPrefWidth(200);
        volumeLable.setFill(Color.WHITE);

        // Event handler for the volume slider
        HBox volume = new HBox();
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> mp.setVolume(newValue.doubleValue()));
        volume.getChildren().add(volumeLable);
        volume.getChildren().add(volumeSlider);
        volume.setAlignment(Pos.CENTER);
        middle.getChildren().add(volume);
/*
        Button onOffButton = new Button();
        onOffButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
        if(bd.getDifficultMode()){
            image[0] = new Image(getClass().getResourceStream("/red.png");
        }
        else {
            image[0] = new Image(getClass().getResourceStream("/blue.png");
        }
        imageView[0] = new ImageView(image[0]);
        imageView[0].setFitWidth(80);
        imageView[0].setFitHeight(70);
        onOffButton.setGraphic(imageView[0]);

        HBox difficulty = new HBox();
        Text mode = new Text("Hard Mode: ");
        mode.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.4));
        mode.setFill(Color.WHITE);

        onOffButton.setOnAction(event -> {
            if(bd.getDifficultMode()){
                image[0] = new Image(getClass().getResourceStream("/blue.png");
            }
            else {
                image[0] = new Image(getClass().getResourceStream("/red.png");
            }
            imageView[0] = new ImageView(image[0]);
            imageView[0].setFitWidth(80);
            imageView[0].setFitHeight(70);
            onOffButton.setGraphic(imageView[0]);
            bd.setDifficultMode(!bd.getDifficultMode());
        });
        difficulty.getChildren().add(mode);
        difficulty.getChildren().add(onOffButton);
        difficulty.setAlignment(Pos.CENTER);
        difficulty.setSpacing(20);
        middle.getChildren().add(difficulty);

 */

        Button restart = new Button("New Game");
        restart.setFont(Font.font("Times New Roman", size*.3));
        restart.setPrefSize(size*2, size*.75);
        restart.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
        middle.getChildren().add(restart);
        middle.setAlignment(Pos.CENTER);
        root.getChildren().add(middle);

        root.getChildren().add(new Rectangle(10000, 3, Color.WHITE));
        Text statistics = new Text("Statistics          ");
        statistics.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.8)); // Set font and size
        statistics.setFill(Color.WHITE);
        root.getChildren().add(statistics);
        root.getChildren().add(makeStats());
        root.getChildren().add(makeGraph());


        root.setSpacing(15);

        Scene scene = new Scene(root, screenWidth, screenHeight);

        settings.setOnAction(event -> {
            mainStage.setScene(gameWindow()); // Replace the scene with the game scene
            bd.setPause(false);
        });

        restart.setOnAction(event -> {
            try {
                bd.reset();
                message.setText("");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bd.setPause(false);
            mainStage.setScene(gameWindow()); // resets the scene
        });
        image[0] = new Image(getClass().getResource("/background.png").toString());
        root.setBackground(new Background(new BackgroundImage(
                image[0],
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                null,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
        )));

        return scene;
    }

    private VBox makeVert(String n, String t){//helper for displaying the stats in the settings menu
        Text num = new Text(n);
        num.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.6));
        num.setFill(Color.WHITE);
        Text text = new Text(t);
        text.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.4));
        text.setFill(Color.WHITE);
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(num);
        root.getChildren().add(text);
        return root;
    }

    private HBox makeStats(){//makes the stats screen
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);

        root.getChildren().add(makeVert(bd.getNumPlayed()+"", "Played"));
        root.getChildren().add(makeVert((int)(bd.getPercentWon()*100)+"", "Win %"));
        root.getChildren().add(makeVert(bd.getCurStreak()+"", "Current\nStreak"));
        root.getChildren().add(makeVert(bd.getMaxStreak()+"", "Max\nStreak"));

        root.setSpacing(60);
        root.setStyle("-fx-background-color: rgba(10, 10, 10, 0.80);");
        return root;
    }

    private VBox makeGraph(){//makes the graph for the stats screen
        VBox graph = new VBox();
        Text guessDis = new Text("\nGuess Distribution\n");
        guessDis.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.5));
        guessDis.setFill(Color.WHITE);
        graph.getChildren().add(guessDis);
        for(int i = 0; i < 6; i++){
            Rectangle recht = new Rectangle(0, 15, Color.WHITE);
            if(bd.getPrevGuesses().size() -1 == i){
                recht.setFill(Color.LIGHTSEAGREEN);
            }
            if(bd.getGuessDistribution()[i] == 0){
                recht.setWidth(10);
            }else {
                recht.setWidth(10 + ((double) bd.getGuessDistribution()[i]/bd.getNumPlayed()) * 250);
            }
            Text cur = new Text((i+1)+"");
            cur.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.3));
            cur.setFill(Color.WHITE);
            Text num = new Text(bd.getGuessDistribution()[i]+"");
            num.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.3));
            num.setFill(Color.BLACK);
            HBox hbox = new HBox(cur, recht, num);
            hbox.setAlignment(Pos.BASELINE_LEFT);
            hbox.setTranslateX(120);
            num.setTranslateX(num.getText().length()*(-10) -1);
            num.setTranslateY(-2);
            cur.setTranslateY(-2);
            graph.getChildren().add(hbox);
        }
        graph.getChildren().add(new Text("\n"));
        graph.setAlignment(Pos.CENTER);
        graph.setStyle("-fx-background-color: rgba(10, 10, 10, 0.80);");
        return graph;
    }

    private Scene gameWindow(){//makes the game window
        VBox root = new VBox();
        HBox top = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(5), Insets.EMPTY)));

        settings = new Button("");
        settings.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
        Image image = new Image(getClass().getResource("/pause.png").toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        settings.setGraphic(imageView);
        image = new Image(getClass().getResource("/wordlePainting.png").toString());
        imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(50);

        top.getChildren().add(imageView);
        top.getChildren().add(settings);
        top.setSpacing(60);
        top.setAlignment(Pos.CENTER);
        root.getChildren().add(top);
        if(!bd.getPause()) {
            makeGuessingSpots();
        }
        root.getChildren().add(squaresGrid);
        message.setFont(Font.font("Times New Roman", FontWeight.THIN, size*.4)); // Set font and size
        message.setTextFill(Color.BLACK);
        if(message.getText().equals("")){
            message.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
        }
        if(!bd.getPause()){
            makeKeyboard();
        }
        root.getChildren().add(keyboardBox);
        root.getChildren().add(new Region());

        image = new Image(getClass().getResource("/background.png").toString());
        root.setBackground(new Background(new BackgroundImage(
                image,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                null,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
        )));

        StackPane sp = new StackPane(root, message);
        sp.setAlignment(message, Pos.TOP_CENTER);
        message.setTranslateY(60);

        Scene scene = new Scene(sp, screenWidth, screenHeight);

        //add keyboard typing functionality
        scene.setOnKeyPressed(key -> {
            if(key.getCode() == KeyCode.SHIFT) {
                handleKeyPress("Enter");
            }
            else if(key.getCode() == KeyCode.BACK_SPACE){
                handleKeyPress("⌫");
            }
            else if("QWERTYUIOPASDFGHJKLZXCVBNM".contains(key.getText().toUpperCase())){
                handleKeyPress(key.getText().toUpperCase());
            }
        });
        settings.setOnAction(event -> {
            mainStage.setScene(settingsWindow()); // Replace the scene with the settings scene
            bd.setPause(true);
        });
        return scene;
    }

    private void makeGuessingSpots(){
        // Create a GridPane to hold the squares
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        // Populate grid with squares using Block which holds text and rechtangle
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                squares[i][j] = new Block("", size);
                grid.add(squares[i][j], j, i);
            }
        }
        squaresGrid = grid;
    }

    private void makeKeyboard() {// make the keyboard on the screen
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        for (int i = 0; i < keys.length; i++) {
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(5);
            grid.setVgap(5);
            keyboard[i] = new Button[keys[i].length];
            for (int j = 0; j < keys[i].length; j++) {
                String key = keys[i][j];
                keyboard[i][j] = new Button(key);
                if(keys[i][j].equals("Enter") || keys[i][j].equals("⌫")){
                    keyboard[i][j].setPrefSize(60, 40);
                }
                else {
                    keyboard[i][j].setPrefSize(40, 40);
                }
                keyboard[i][j].setBackground(new Background(new BackgroundFill(Color.LIGHTSEAGREEN, new CornerRadii(5), Insets.EMPTY)));
                keyboard[i][j].setTextFill(Color.WHITE);
                keyboard[i][j].setFont(Font.font("Times New Roman", size*.3));
                keyboard[i][j].setOnAction(e -> handleKeyPress(key));
                grid.add(keyboard[i][j], j, 0);
            }
            root.getChildren().add(grid);
        }
        root.setSpacing(9);
        keyboardBox = root;
    }

    public void changeDisp(String key) {  //change the apperance of the squares to reflect keypresses
        int i = bd.getPrevGuesses().size();
        int j = key.equals("⌫")? bd.getGuess().length(): bd.getGuess().length()-1;

        if (key.equals("⌫")) {
            squares[i][j].getApperance().setStroke(Color.CYAN);
            squares[i][j].getApperance().setFill(Color.TRANSPARENT);
            squares[i][j].getText().setText(" ");
        }else {
            if(key.charAt(0) == 'I' || key.charAt(0) == 'J'){
                squares[i][j].getText().setText(" " + key.charAt(0));
            }
            else {
                squares[i][j].getText().setText(key.charAt(0) + "");
            }
        }
        squares[i][j].getApperance().setStrokeWidth(3);
        squares[i][j].getApperance().setWidth(size-1);
        squares[i][j].getApperance().setHeight(size-1);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {//to animate the boarder of the square when a key is typed
                squares[i][j].getApperance().setStrokeWidth(1.5);
                squares[i][j].getApperance().setWidth(size);
                squares[i][j].getApperance().setHeight(size);
                timer.cancel();
            }
        }, 200);
    }

    public void guessGiven(){  //change the color of the keyboard to reflect guesses
        int i = bd.getPrevGuesses().size()-1;
        Color[] arr = bd.findColors();
        for(int k = 0; k < 5; k++){
            Timer timer = new Timer();
            int finalK = k;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {//animate flip and incremental showing of letters
                    Timeline tl = new Timeline();
                    tl.getKeyFrames().add(new KeyFrame(new Duration(150), new KeyValue(squares[i][finalK].getApperance().scaleYProperty(), 0)));
                    tl.getKeyFrames().add(new KeyFrame(new Duration(150), new KeyValue(squares[i][finalK].getText().scaleYProperty(), 0)));
                    if(bd.getPrevGuesses().get(i).charAt(finalK) == 'I' || bd.getPrevGuesses().get(i).charAt(finalK) == 'J'){
                        squares[i][finalK].getText().setText(" " + bd.getPrevGuesses().get(i).charAt(finalK));
                    }
                    else{squares[i][finalK].getText().setText(bd.getPrevGuesses().get(i).charAt(finalK)+"");}
                    squares[i][finalK].getApperance().setFill(arr[finalK]);
                    squares[i][finalK].getApperance().setStroke(arr[finalK]);
                    for(int r = 0; r < keys.length; r++){
                        for(int c = 0; c < keys[r].length; c++){
                            if(bd.getPrevGuesses().get(i).charAt(finalK) == keys[r][c].charAt(0) && keys[r][c].length() == 1){
                                Color bgColor = (Color) keyboard[r][c].getBackground().getFills().get(0).getFill();
                                if(!bgColor.equals(Color.STEELBLUE) && !bgColor.equals(Color.ORANGE)){
                                    if(arr[finalK].equals(Color.GRAY)){
                                        keyboard[r][c].setDisable(true);
                                        if(!bd.getWord().contains(keyboard[r][c].getText().substring(0,1))){
                                            bd.getCantUse().add(keyboard[r][c].getText().charAt(0));
                                        }
                                    }
                                    keyboard[r][c].setBackground(new Background(new BackgroundFill(arr[finalK], new CornerRadii(5), Insets.EMPTY)));
                                }
                            }
                        }
                    }
                    tl.getKeyFrames().add(new KeyFrame(new Duration(300), new KeyValue(squares[i][finalK].getApperance().scaleYProperty(), 1)));
                    tl.getKeyFrames().add(new KeyFrame(new Duration(300), new KeyValue(squares[i][finalK].getText().scaleYProperty(), 1)));
                    tl.setDelay(new Duration(finalK+1));
                    tl.play();
                    timer.cancel();
                }
            }, finalK*100);
        }
    }

    private void handleKeyPress(String key) {  //does things depending on letter pressed
        if(key.equals("Escape")){
            bd.setPause(true);
        }
        else if (key.equals("Enter") && !bd.getPrevGuesses().contains(bd.getWord())) {
            int state = bd.checkGuess();
            PauseTransition delay = new PauseTransition(new Duration(1500));
            delay.setOnFinished(event -> {
                mainStage.setScene(settingsWindow());
                bd.setPause(true);
            });
            switch (state){
                case 1:
                    message.setTextFill(Color.BLACK);
                    message.setText("Correct!!!");
                    message.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
                    delay.play();
                    break;
                case -1:
                    message.setTextFill(Color.BLACK);
                    message.setText("Not enough letters");
                    message.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
                    break;
                case -2:
                    message.setTextFill(Color.BLACK);
                    message.setText("Not in word list");
                    message.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
                    break;
                case -3:
                    message.setTextFill(Color.BLACK);
                    message.setText("Game over!\nThe word was: " + bd.getWord());
                    message.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
                    delay.play();
                    break;
                case -4:
                    message.setTextFill(Color.BLACK);
                    message.setText("Hard Mode: did not use all info");
                    message.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));
                    break;
            }
            Timer timer = new Timer();  //gets rid of message after some time
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(state != 1 && state != -3){
                        message.setTextFill(Color.TRANSPARENT);
                        message.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
                    }
                    timer.cancel();
                }
            }, 1000);
            if(state == 0 || state == 1 || state == -3) {
                guessGiven();
            }
        } else if (key.equals("⌫")) {
            if(bd.getGuess().length() > 0){
                bd.setGuess(bd.getGuess().substring(0, bd.getGuess().length()-1));
                changeDisp(key);
            }
        } else {
            String curGuess = bd.getGuess();
            if(!bd.getPause()) {
                if (curGuess.length() < 5 && bd.getPrevGuesses().size() < 6 && !bd.getPrevGuesses().contains(bd.getWord()) && !bd.getCantUse().contains(key.charAt(0))) {
                    bd.setGuess(curGuess + key);
                    changeDisp(key);
                }
            }
        }
    }


    public static void main(String[] args) {
        launch();
    }
}