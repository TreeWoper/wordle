package mainApp;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Block extends javafx.scene.Group{
    private Text letter;
    private Rectangle apperance;

    Block(String l, int a){
        apperance = new Rectangle(a, a);
        apperance.setStrokeWidth(1.5);
        apperance.setStroke(Color.CYAN);
        apperance.setFill(Color.TRANSPARENT);

        letter = new Text(l);
        letter.setFont(Font.font("Times New Roman", FontWeight.THIN, a*.6));
        letter.setFill(Color.LIGHTCYAN);
        letter.setLayoutX((apperance.getWidth()-36));
        letter.setLayoutY((apperance.getHeight()-15));

        getChildren().addAll(apperance, letter);
    }

    public Text getText() { return letter; }
    public Rectangle getApperance() {
        return apperance;
    }
}
