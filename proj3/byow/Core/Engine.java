package byow.Core;

import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import org.antlr.v4.runtime.misc.Utils;
import org.apache.commons.lang3.StringUtils;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Long.parseLong;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    boolean active;
    TETile[][] MazeMap;
    String inputs;
    String gameState;
    ArrayList<Character> moveSet;
    Font myFont = new Font("Arial", Font.BOLD, 50);
    int enteredSeed;
    int xPos;
    int yPos;
    TERenderer myDraw = new TERenderer();
    Maze myMaze;
    boolean[][] coinMap;
    int coinsCollected;
    int totalCoins;
    Random r;
    File saveFile;

    public Engine() {
        moveSet = new ArrayList<Character>();
        moveSet.add('w');
        moveSet.add('a');
        moveSet.add('s');
        moveSet.add('d');
        inputs = "";
        coinsCollected = 0;
        totalCoins = 0;

    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        active = true;
        KeyboardInputSource input = new KeyboardInputSource();
        mainMenu();

        while (active) {
            char typer = input.getNextKey();
            process(Character.toLowerCase(typer));
            if (gameState.equals("Maze")) {
                myDraw.renderFrame(MazeMap);
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // 0 = void, 1 = floor, 2 = wall, 3 = room floor, 4 = room wall. W = 80, H = 30
        int[][] worldMap = new int[WIDTH][HEIGHT];
        TETile[][] myMap = new TETile[WIDTH][HEIGHT];
        Long seed = parseLong(input.substring(1, input.length() - 1));
        Random myRan = new Random(seed);

        Maze aMazeing = new Maze(myRan, WIDTH, HEIGHT);
        worldMap = aMazeing.giveIntMap();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (worldMap[x][y] == 0) {
                    myMap[x][y] = Tileset.NOTHING;
                } else if (worldMap[x][y] == 1) {
                    myMap[x][y] = Tileset.FLOOR;
                } else if (worldMap[x][y] == 2) {
                    myMap[x][y] = Tileset.WALL;
                } else if (worldMap[x][y] == 3) {
                    myMap[x][y] = Tileset.AVATAR;
                } else if (worldMap[x][y] == 4) {
                    myMap[x][y] = Tileset.COIN;
                }
            }
        }
        return myMap;
    }

    private void process(char c) {
        if (gameState.equals("Over")) {
            if (Character.isDefined(c)) {
                try {
                    Utils.writeFile("./save.txt", inputs);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.exit(0);
            }
        }
        if (c == 'q') {
            if (gameState.equals("Maze")) {
                gameover();
            }
            else {
                System.exit(0);
            }
        }
        if (gameState.equals("Menu")) {
            if (c == 'n') {     
                inputs += c;
                enteredSeed = 0;
                seedMenu();
            }
            if (c == 'z') {
                showControls();
            }
            if (c == 'l' && new File("./save.txt").exists()) {
                loadGame();
            }
        } else if (gameState.equals("Controls")) {
            if (c == 'r') {
                mainMenu();
            }
        } else if (gameState.equals("seedMenu")) {
            inputs += c;
            if (c == 's') {
                if (enteredSeed != 0) {
                    MazeGame();
                }
            }
            if (Character.isDigit(c)) {
                if (enteredSeed == 0) {
                    enteredSeed = Character.getNumericValue(c);
                } else {
                    enteredSeed *= 10;
                    enteredSeed += Character.getNumericValue(c);
                }
                seedMenu();
            }
        } else if (gameState.equals("Maze")) {
            inputs += c;
            if (moveSet.add(c)) {
                moveMe(c);
            } if (c == 'p') {
                collectCoin();
            }
        }
    }

    public void gameover() {
        overScreen();
    }

    public void overScreen() {
        gameState = "Over";
        StdDraw.setFont(myFont);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.6, "You collected " + totalCoins + " dollars!");
        StdDraw.setPenColor(Color.red);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.3, "Press any key to exit");
        StdDraw.setPenColor(Color.white);
        StdDraw.show();
        StdDraw.pause(5);
    }

    public void collectCoin() {
        if (coinMap[xPos][yPos]) {
            coinsCollected += 1;
            totalCoins += 1;
            coinMap[xPos][yPos] = false;
        }
        if (coinMap[xPos + 1][yPos]) {
            coinsCollected += 1;
            totalCoins += 1;
            coinMap[xPos + 1][yPos] = false;
            MazeMap[xPos + 1][yPos] = Tileset.FLOOR;
        }
        if (coinMap[xPos - 1][yPos]) {
            coinsCollected += 1;
            totalCoins += 1;
            coinMap[xPos - 1][yPos] = false;
            MazeMap[xPos - 1][yPos] = Tileset.FLOOR;
        }
        if (coinMap[xPos][yPos + 1]) {
            coinsCollected += 1;
            totalCoins += 1;
            coinMap[xPos][yPos + 1] = false;
            MazeMap[xPos][yPos + 1] = Tileset.FLOOR;
        }
        if (coinMap[xPos][yPos - 1]) {
            coinsCollected += 1;
            totalCoins += 1;
            coinMap[xPos][yPos - 1] = false;
            MazeMap[xPos][yPos - 1] = Tileset.FLOOR;
        }
        if (coinsCollected >= myMaze.coinsOnMap()) {
            coinsCollected = 0;
            long l = r.nextInt(10000);
            r = new Random(l);
            myMaze = new Maze(r, WIDTH, HEIGHT);
            MazeMap = myMaze.giveTileMap();
            coinMap = myMaze.cMap();
            xPos = 40;
            yPos = 5;
        }

    }

    public void moveMe(char c) {
        if (c == 'w') {
            TETile tile = MazeMap[xPos][yPos + 1];
            if (tile.equals(Tileset.FLOOR) || tile.equals(Tileset.COIN)) {
                MazeMap[xPos][yPos + 1] = Tileset.AVATAR;
                if (coinMap[xPos][yPos]) {
                    MazeMap[xPos][yPos] = Tileset.COIN;
                } else {
                    MazeMap[xPos][yPos] = Tileset.FLOOR;
                }
                yPos += 1;
            }
        } else if (c == 's') {
            TETile tile = MazeMap[xPos][yPos - 1];
            if (tile.equals(Tileset.FLOOR) || tile.equals(Tileset.COIN)) {
                MazeMap[xPos][yPos - 1] = Tileset.AVATAR;
                if (coinMap[xPos][yPos]) {
                    MazeMap[xPos][yPos] = Tileset.COIN;
                } else {
                    MazeMap[xPos][yPos] = Tileset.FLOOR;
                }
                yPos -= 1;
            }
        } else if (c == 'a') {
            TETile tile = MazeMap[xPos - 1][yPos];
            if (tile.equals(Tileset.FLOOR) || tile.equals(Tileset.COIN)) {
                MazeMap[xPos - 1][yPos] = Tileset.AVATAR;
                if (coinMap[xPos][yPos]) {
                    MazeMap[xPos][yPos] = Tileset.COIN;
                } else {
                    MazeMap[xPos][yPos] = Tileset.FLOOR;
                }
                xPos -= 1;
            }
        } else if (c == 'd') {
            TETile tile = MazeMap[xPos + 1][yPos];
            if (tile.equals(Tileset.FLOOR) || tile.equals(Tileset.COIN)) {
                MazeMap[xPos + 1][yPos] = Tileset.AVATAR;
                if (coinMap[xPos][yPos]) {
                    MazeMap[xPos][yPos] = Tileset.COIN;
                } else {
                    MazeMap[xPos][yPos] = Tileset.FLOOR;
                }
                xPos += 1;
            }
        }
    }



    public void MazeGame() {
        new File("./save.text").delete();
        saveFile = new File ("./save.txt");
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gameState = "Maze";
        long longseed = enteredSeed;
        r = new Random(longseed);
        myMaze = new Maze(r, WIDTH, HEIGHT);
        MazeMap = myMaze.giveTileMap();
        coinMap = myMaze.cMap();
        myDraw.initialize(90, 40);
        xPos = 40;
        yPos = 5;
    }

    public void loadGame() {
        char[] oldIN;
        try {
            oldIN = Utils.readFile("./save.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < oldIN.length; i++) {
            process(oldIN[i]);
        }
    }



    public void seedMenu() {
        gameState = "seedMenu";
        StdDraw.setFont(myFont);
        StdDraw.clear(Color.BLACK);

        StdDraw.text(WIDTH / 2, HEIGHT * 0.6, "Enter your seed!");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.5, Integer.toString(enteredSeed));
        StdDraw.text(WIDTH / 2, HEIGHT * 0.4, "Press S when ready.");
        StdDraw.show();
        StdDraw.pause(5);
    }

    public void showControls() {
        gameState = "Controls";
        StdDraw.setFont(myFont);
        StdDraw.clear(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.6, "W, A, S, D configuration to move!");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.4, "Press P to collect the money!.");
        StdDraw.setPenColor(Color.red);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.3, "Return to Menu (R)");
        StdDraw.setPenColor(Color.white);
        StdDraw.show();
        StdDraw.pause(5);

    }

    public void mainMenu() {
        gameState = "Menu";
        StdDraw.setCanvasSize(WIDTH*20, HEIGHT*20);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setFont(myFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.6, "Maze Crawler");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.5, "New Game (N)");


        if (new File("./save.txt").exists()) {
            StdDraw.text(WIDTH / 2, HEIGHT * 0.4, "Load Game (L)");
        } else {
            StdDraw.setPenColor(Color.red);
            StdDraw.text(WIDTH / 2, HEIGHT * 0.4, "Load Game (L), but no save exists");
            StdDraw.setPenColor(Color.WHITE);
        }
        StdDraw.text(WIDTH / 2, HEIGHT * 0.3, "Quit (Q)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.2, "Show Controls (Z)");

        StdDraw.show();
        StdDraw.pause(5);
    }



}
