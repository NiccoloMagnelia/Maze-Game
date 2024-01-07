package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Random;

public class Maze {
    boolean[][] coinMap;
    Random generator;
    int width;
    int height;
    int[][] intMap;
    TETile[][] myMap;
    int xPos;
    int yPos;
    int coins;


    public Maze(Random seed, int w, int h) {
        coins = 0;
        intMap = new int[w][h];
        coinMap = new boolean[w][h];
        generator = seed;
        width = w - 5;
        height = h - 5;
        for (int i = 0; i < w; i++) {
            for (int k = 0; k < h; k++) {
                intMap[i][k] = 0;
                coinMap[i][k] = false;
            }
        }
        randomFloor();
        Walls();
        intMap[40][5] = 3;
        xPos = 40;
        yPos = 5;
    }

    public void randomFloor() {
        int numRooms = RandomUtils.uniform(generator, 13, 19);
        int count = 0;
        int lastX = 0;
        int lastY= 0;
        int curX = 0;
        int curY = 0;
        while (count < numRooms) {
            int x = RandomUtils.uniform(generator, 5, width - 8);
            int y = RandomUtils.uniform(generator, 5, height - 7);
            int roomW = generator.nextInt(5) + 3;
            int roomY = generator.nextInt(3) + 2;
            for (int i = x; i < x + roomW; i++) {
                for (int k = y; k < y + roomY; k++) {
                    if (intMap[i][k] == 0) {
                        intMap[i][k] = 1;
                    }
                    if (i == x + (roomW / 2) && k == y + (roomY / 2)) {
                        curX = i;
                        curY = k;

                    }
                }
            }
            Halls(curX, curY, 40, 25);
            count += 1;
        }
        for (int y = 3; y < height - 2; y++) {
            intMap[40][y] = 1;
        }
    }

    public void Halls(int originX, int originY, int destinationX, int destinationY) {
        int XMove = destinationX - originX;
        int YMove = destinationY - originY;
        if (XMove < 0) {
            int tempX = destinationX;
            destinationX = originX;
            originX = tempX;
            XMove = destinationX - originX;
        }
        if (YMove < 0) {
            int tempY = destinationY;
            destinationY = originY;
            originY = tempY;
            YMove = destinationY - originY;
        }

        int size = 0;
        if (generator.nextInt(4) == 0) {
            size = 2;
        } else {
            size = 1;
        }

        for (int i = originX; i < destinationX; i++) {
            if (intMap[i][originY] == 1 && (intMap[i][originY + 1] == 0 && intMap[i][originY - 1] == 0)) {
                i += 100;
            } else {
                intMap[i][originY] = 1;
            }
        }
        for (int k = originY; k < destinationY; k++) {
            if (intMap[destinationX - 1][k] == 1 && (intMap[destinationX - 1][k + 1] == 0 || intMap[destinationX - 1][k - 1] == 0)) {
                k += 100;
            } else {
                intMap[destinationX][k] = 1;
            }
        }

        if (size == 2) {
            for (int i = originX; i < destinationX; i++) {
                if (intMap[i][originY + 1] == 1 && i > 5) {
                    i += 100;
                } else {
                    intMap[i][originY + 1] = 1;
                }
            }
            for (int k = originY; k < destinationY; k++) {
                if (intMap[destinationX + 1][originY] == 1 && k > 5) {
                    k += 100;
                } else {
                    intMap[destinationX + 1][k] = 1;
                }
            }

        }
    }

    public void Walls() {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (intMap[x][y] == 0) {
                    if (!(intMap[x][y - 1] != 1
                            && intMap[x][y + 1] != 1
                            && intMap[x + 1][y] != 1
                            && intMap [x - 1][y] != 1
                            && intMap [x - 1][y - 1] != 1
                            && intMap [x - 1][y + 1] != 1
                            && intMap [x + 1][y - 1] != 1
                            && intMap [x + 1][y + 1] != 1
                    )) {
                        intMap[x][y] = 2;
                    }
                } else if (intMap[x][y] == 1) {
                    int r = generator.nextInt(15);
                    if (r == 0) {
                        intMap[x][y] = 4;
                        coinMap[x][y] = true;
                        coins += 1;
                    }
                }
            }
        }
    }


    public TETile[][] giveTileMap() {
        TETile[][] Mazer = new TETile[width + 5][height + 5];
        for (int x = 0; x < width + 5; x++) {
            for (int y = 0; y < height + 5; y++) {
                if (intMap[x][y] == 0) {
                    Mazer[x][y] = Tileset.NOTHING;
                } else if (intMap[x][y] == 1) {
                    Mazer[x][y] = Tileset.FLOOR;
                } else if (intMap[x][y] == 2) {
                    Mazer[x][y] = Tileset.WALL;
                } else if (intMap[x][y] == 3) {
                    Mazer[x][y] = Tileset.AVATAR;
                } else if (intMap[x][y] == 4) {
                    Mazer[x][y] = Tileset.COIN;
                }
            }
        }
        return Mazer;
    }





    public int[][] giveIntMap() {
        return intMap;
    }

    public int coinsOnMap() {
        return coins;
    }

    public boolean[][] cMap() {
        return coinMap;
    }

}
