package mainApp;

import javafx.scene.paint.Color;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BoardData {
    private String word;
    private String guess;
    private int numPlayed;
    private double percentWon;
    private int curStreak;
    private int maxStreak;
    private List<String> prevGuesses;
    private List<String> validWords;
    private int[] guessDistribution;
    private List<Integer> prevRandomVal;
    private boolean pause = false;
    private boolean difficultMode = false;
    private List<Character> cantUse;//to disable buttons
    private List<Character> grayGuess; //basicly we map the letter guessed to a list of all the places it was guessed for green and orange
    private Map<Character, List<Integer>> orangeGuess;  //where it shouldnt be
    private Map<Character, List<Integer>> greenGuess; //where it should be

    BoardData() throws FileNotFoundException {
        //add all possible words to a list
        Scanner s = new Scanner(getClass().getResourceAsStream("/listOfWords.txt"));
        validWords = new ArrayList<>();
        prevGuesses = new ArrayList<>();
        prevRandomVal = new ArrayList<>();
        while(s.hasNext()){
            validWords.add(s.next().toUpperCase());
        }
        //initialize stuff
        guess = "";
        grayGuess = new ArrayList<>();
        orangeGuess = new TreeMap<>();
        greenGuess = new TreeMap<>();
        cantUse = new ArrayList<>();

        //read in previously saved game data
        s = new Scanner(getClass().getResourceAsStream("/data.txt"));
        s.next();
        numPlayed = s.nextInt();
        s.next();
        percentWon = s.nextDouble();
        s.next();
        curStreak = s.nextInt();
        s.next();
        maxStreak = s.nextInt();
        s.next();
        guessDistribution = new int[6];
        for(int i = 0; i < 6; i++){
            guessDistribution[i] = s.nextInt();
        }
        s.next();
        while(s.hasNext()){
            prevRandomVal.add(s.nextInt());
        }
        //pick a random word that has not been picked before
        Random random = new Random();
        int randIndex = random.nextInt(validWords.size());
        while(prevRandomVal.contains(randIndex)){
            randIndex = random.nextInt(validWords.size());
        }
        word = validWords.get(randIndex);
        prevRandomVal.add(randIndex);
        System.out.println(word);
    }

    public void reset() throws FileNotFoundException {
        //reset the shadow information for a new game
        prevGuesses = new ArrayList<>();
        Random random = new Random();
        int randIndex = random.nextInt(validWords.size());
        while(prevRandomVal.contains(randIndex)){
            randIndex = random.nextInt(validWords.size());
        }
        word = validWords.get(randIndex);
        prevRandomVal.add(randIndex);
        System.out.println(word);
        guess = "";
        grayGuess = new ArrayList<>();
        orangeGuess = new TreeMap<>();
        greenGuess = new TreeMap<>();
        cantUse = new ArrayList<>();
    }

    public void updateData(boolean win){
        //write to the file to save the game data for the future
        try {
            numPlayed++;
            percentWon = ((numPlayed - 1) *percentWon + (win ? 1: 0)) / numPlayed;
            curStreak = win? curStreak+1: 0;
            guessDistribution[prevGuesses.size()-1] += win? 1: 0;
            if(curStreak > maxStreak){
                maxStreak = curStreak;
            }
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "\\src\\main\\resources\\data.txt");
            file.write( "played: " + numPlayed +
                            "\npercentageWin: " + percentWon +
                            "\ncurrentStreak: " + curStreak +
                            "\nmaxStreak: " + maxStreak +
                            "\nguessDistribution: " + guessDistribution[0] + " " + guessDistribution[1] + " " + guessDistribution[2] + " " + guessDistribution[3] + " " + guessDistribution[4] + " " + guessDistribution[5]);
            file.write("\nprevRandomVal: ");
            if(prevRandomVal.size() != validWords.size()){
                for(int i: prevRandomVal){
                    file.write(i+" ");
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getStats(){//just a test method used earlier
        return ("played: " + numPlayed +
                "\npercentageWin: " + (int) (percentWon*100) +
                "\ncurrentStreak: " + curStreak +
                "\nmaxStreak: " + maxStreak);
    }

    public List<Character> getCantUse() {
        return cantUse;
    }

    public boolean getDifficultMode() {//was going to make difficutlt mode but it was a lot of work
        return difficultMode;
    }

    public void setDifficultMode(boolean difficultMode) {
        this.difficultMode = difficultMode;
    }

    public int getNumPlayed() {
        return numPlayed;
    }

    public double getPercentWon() {
        return percentWon;
    }

    public int getCurStreak() {
        return curStreak;
    }

    public int getMaxStreak() {
        return maxStreak;
    }

    public String getGuess() {
        return guess;
    }
    public void setGuess(String guess) {
        this.guess = guess;
    }

    public List<String> getPrevGuesses() {
        return prevGuesses;
    }

    public int checkGuess(){//1 = correct, 0 = incorrect, -1 = not valid guess, -2 = not a real word, -3 = game over, -4 cannot use because of hard mode
        if(guess.length() < 5){
            return -1;
        }
        else if(!validWords.contains(guess)){
            return -2;
        }
        else if(guess.equals(word)){
            prevGuesses.add(guess);
            guess = "";
            pause = true;
            updateData(true);
            return 1;
        }
        else{
            /*
            if(difficultMode){
                for(int i = 0; i < guess.length(); i++){
                    if(greenGuess.containsKey(guess.toUpperCase().charAt(i))){
                        List<Integer> list = greenGuess.get(guess.toUpperCase().charAt(i));
                        for(int j = 0; j < list.size(); j++){
                            if(i != list.get(j)){
                                return -4;
                            }
                        }
                    }
                    else if(orangeGuess.containsKey(guess.charAt(i))){
                        List<Integer> list = orangeGuess.get(guess.toUpperCase().charAt(i));
                        for(int j = 0; j < list.size(); j++){
                            if(i == list.get(j)){
                                return -4;
                            }
                        }
                    }
                    else if(grayGuess.contains(guess.charAt(i))){
                        return -4;
                    }
                    else if(prevGuesses.size() > 1){
                        return -4;
                    }
                }
            }*/
            prevGuesses.add(guess);
            guess = "";
            if(prevGuesses.size() == 6){
                pause = true;
                updateData(false);
                return -3;
            }
            return 0;
        }
    }

    public Color[] findColors(){
        //claifies the color of the letter based on the guess and the actual word
        Color[] ans = new Color[word.length()];
        String prevGuess = prevGuesses.get(prevGuesses.size()-1);
        boolean[] visited = new boolean[5];
        for(int i = 0; i < 5; i++){
            visited[i] = false;
            ans[i] = Color.GRAY;
        }
        for(int i = 0; i < 5; i++){
            if(word.charAt(i) == prevGuess.charAt(i)){
                ans[i] = Color.GREEN;
                visited[i] = true;
            }
        }
        for(int i = 0; i < 5; i++){
            if(ans[i].equals(Color.GRAY)) {
                for (int j = 0; j < 5; j++) {
                    if (!visited[j] && word.charAt(j) == prevGuess.charAt(i)) {
                        ans[i] = Color.ORANGE;
                        visited[j] = true;
                    }
                }
            }
        }
        if(difficultMode){
            for(int i = 0; i < ans.length; i++){
                if(ans[i].equals(Color.GRAY)){
                    grayGuess.add(prevGuess.charAt(i));
                }
                else if(ans[i].equals(Color.ORANGE)){
                    if(orangeGuess.containsKey(prevGuess.charAt(i))){
                        orangeGuess.get(prevGuess.charAt(i)).add(i);
                    }
                    else{
                        orangeGuess.put(prevGuess.charAt(i), new ArrayList<>());
                    }
                }
                else{
                    if(greenGuess.containsKey(prevGuess.charAt(i))){
                        greenGuess.get(prevGuess.charAt(i)).add(i);
                    }
                    else{
                        greenGuess.put(prevGuess.charAt(i), new ArrayList<>());
                    }
                }
            }
        }
        return ans;
    }

    public int[] getGuessDistribution(){
        return guessDistribution;
    }

    public Map<Character, List<Integer>> getGreenGuess() {
        return greenGuess;
    }

    public Map<Character, List<Integer>> getOrangeGuess() {
        return orangeGuess;
    }

    public List<Character> getGrayGuess() {
        return grayGuess;
    }

    public boolean getPause() {
        return pause;
    }
    public void setPause(boolean p) {
        pause = p;
    }

    public String getWord() {
        return word;
    }
}