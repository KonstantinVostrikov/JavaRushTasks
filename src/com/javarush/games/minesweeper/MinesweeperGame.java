package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private final static String MINE = "\uD83D\uDCA3";
    private final static String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x,y,"");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        List<GameObject> result = new ArrayList<>();
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (!gameField[y][x].isMine) {
                    result = getNeighbors(gameField[y][x]);
                    for (GameObject gameObject : result) {
                        if (gameObject.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }

        }
    }

    private void openTile(int x, int y) {
        if (!gameField[y][x].isOpen && !gameField[y][x].isFlag && !isGameStopped) {

            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameField[y][x].isOpen = true;
                gameOver();
                countClosedTiles--;
            } else {

                //если количество соседей-мин больше нуля - выводим число, а если меньше - оставляем клетку пустой, только помечаем, как открытую и красим в зеленый + уменьшаем количество закрытых ячеек
                if (gameField[y][x].countMineNeighbors > 0) {
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                } else {
                    setCellValue(x, y, "");
                }
                gameField[y][x].isOpen = true;
                setCellColor(x, y, Color.GREEN);
                countClosedTiles--;
                score += 5;
                setScore(score);


                if (gameField[y][x].countMineNeighbors == 0) {
                    List<GameObject> neighbors = getNeighbors(gameField[y][x]);
                    for (GameObject gameObject : neighbors) {
                        if (!gameObject.isOpen) {
                            openTile(gameObject.x, gameObject.y);
                        }
                    }
                }




                if (countClosedTiles == countMinesOnField){
                    win();
                }
            }
        }

    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        if (isGameStopped){
            restart();
        } else
        openTile(x, y);
    }

    private void markTile(int x, int y) {
        if (!isGameStopped) {
            if (gameField[y][x].isOpen == true || countFlags == 0 && gameField[y][x].isFlag == false) {

            }
            //если ячейка не отрыта или есть доступные флаги или убираем флаг
            else {
                if (gameField[y][x].isFlag == false) {
                    gameField[y][x].isFlag = true;
                    countFlags--;
                    setCellValue(x, y, FLAG);
                    setCellColor(x, y, Color.YELLOW);
                } else {
                    gameField[y][x].isFlag = false;
                    countFlags++;
                    setCellValue(x, y, "");
                    setCellColor(x, y, Color.ORANGE);
                }
            }
        }
    }


    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.AQUA, "Game Over!", Color.RED, 42);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.AQUA, "You win! Congratulations!", Color.RED, 42);
    }

    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();

    }
}