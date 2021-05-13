package com.javarush.games.game2048;

import com.javarush.engine.cell.*;

import java.util.Arrays;

public class Game2048 extends Game {
    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score = 0;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }

    private void createGame() {
        gameField = new int[SIDE][SIDE];
        score = 0;
        setScore(score);
        createNewNumber();
        createNewNumber();
    }

    private void drawScene() {

        for (int y = 0; y < gameField.length; y++) {
            for (int x = 0; x < gameField[y].length; x++) {
                setCellColoredNumber(x, y, gameField[y][x]);
            }
        }
    }

    private void createNewNumber() {
        if (getMaxTileValue() == 2048) {
            win();
        }

        while (true) {
            int x = getRandomNumber(SIDE);
            int y = getRandomNumber(SIDE);

            if (gameField[x][y] == 0) {
                int chance = getRandomNumber(10);
                if (chance == 9) {
                    gameField[x][y] = 4;
                } else gameField[x][y] = 2;
                break;
            }
        }
    }


    private void setCellColoredNumber(int x, int y, int value) {
        Color cellColor = getColorByValue(value);
        String text;

        if (value == 0) {
            text = "";
        } else text = String.valueOf(value);

        setCellValueEx(x, y, cellColor, text);
    }

    private Color getColorByValue(int value) {
        switch (value) {
            case 2:
                return Color.LIGHTGOLDENRODYELLOW;
            case 4:
                return Color.PALEGOLDENROD;
            case 8:
                return Color.MOCCASIN;
            case 16:
                return Color.NAVAJOWHITE;
            case 32:
                return Color.PEACHPUFF;
            case 64:
                return Color.PERU;
            case 128:
                return Color.ROSYBROWN;
            case 256:
                return Color.SILVER;
            case 512:
                return Color.THISTLE;
            case 1024:
                return Color.GOLDENROD;
            case 2048:
                return Color.GOLD;
            default:
                return Color.IVORY;
        }
    }

    private boolean compressRow(int[] row) {
        boolean isMoved = false;

        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] == 0 && row[i + 1] != 0) {
                row[i] = row[i + 1];
                row[i + 1] = 0;
                isMoved = true;
                i = -1;
            }
        }

        return isMoved;
    }

    private boolean mergeRow(int[] row) {
        boolean isMerged = false;

        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] != 0 && row[i] == row[i + 1]) {
                row[i] *= 2;
                row[i + 1] = 0;
                score += row[i];
                setScore(score);
                i++;
                isMerged = true;
            }
        }
        return isMerged;
    }

    @Override                                                                                               //движение матрицы и рестарт игры
    public void onKeyPress(Key key) {
        if (isGameStopped){
            if (key == Key.SPACE){
                isGameStopped = false;
                createGame();
                drawScene();
                return;
            } else return;
        }

        if (!canUserMove()){
            gameOver();
            return;
        }

        if (key == Key.LEFT) {
            moveLeft();
        } else if (key == Key.RIGHT) {
            moveRight();
        } else if (key == Key.UP) {
            moveUp();
        } else if (key == Key.DOWN) {
            moveDown();
        } else return;

        drawScene();
    }

    private void rotateClockwise() {
        int[][] rotated = new int[SIDE][SIDE];
        int fillVerticalIndex = 0;          // индекс того, какой по счету строку заполняем
        int fillGorizontalIndex = SIDE - 1; // индекс того, какую по счету ячейку (столбец) заполняем, начинаем с последней по счету ячейки и двигаемся влевопо ячейкам массива с каждой итерацией внешнего цикла

        for (int i = 0; i < gameField.length; i++) {

            for (int j = 0; j < gameField[i].length; j++) {
                rotated[fillVerticalIndex++][fillGorizontalIndex] = gameField[i][j];
            }

            fillVerticalIndex = 0;
            fillGorizontalIndex--;
        }
        gameField = rotated;
    }


    private void moveLeft() {
        boolean needToAdd = false;

        for (int i = 0; i < gameField.length; i++) {

            if (compressRow(gameField[i]) | mergeRow(gameField[i]) | compressRow(gameField[i])) {
                needToAdd = true;
            }
        }

        if (needToAdd) {
            createNewNumber();
        }
    }

    private void moveRight() {
        for (int i = 0; i < 4; i++) {
            rotateClockwise();
            if (i == 1) moveLeft();
            ;
        }
    }

    private void moveUp() {
        for (int i = 0; i < 4; i++) {
            rotateClockwise();
            if (i == 2) moveLeft();
            ;
        }
    }

    private void moveDown() {
        for (int i = 0; i < 4; i++) {
            rotateClockwise();
            if (i == 0) moveLeft();
            ;
        }
    }

    private int getMaxTileValue() {
        int max = 0;

        for (int arr[] : gameField) {
            int currentMax = Arrays.stream(arr).max().getAsInt();
            if (max < currentMax) max = currentMax;
        }

        return max;
    }

    private void win() {
        showMessageDialog(Color.BLACK, "You win!", Color.YELLOW, 32);
        isGameStopped = true;
    }

    private boolean canUserMove() {

        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {

                if (gameField[i][j] == 0) {          //ищем хотя бы один ноль в массиве gameField
                    return true;
                } else if (i < SIDE - 1 && gameField[i][j] == gameField[i + 1][j]) {                 // если не вышли за пределы массива, то находим индекс нижней ячейки
                    return true;                                                                    //если нижняя ячейка существует, то сравниваем текущую с ней по значениям, при совпадении значений возвращаем true

                } else if (j < SIDE - 1 && gameField[i][j] == gameField[i][j + 1]) {                 // если не вышли за пределы массива, то находим индекс правой ячейки
                    return true;                                                                    //если правая ячейка существует, то сравниваем текущую с ней по значениям, при совпадении значений возвращаем true

                }

            }
        }

        return false;
    }

    private void gameOver() {
        showMessageDialog(Color.BLACK, "Game Over!", Color.RED, 32);
        isGameStopped = true;
    }
}
