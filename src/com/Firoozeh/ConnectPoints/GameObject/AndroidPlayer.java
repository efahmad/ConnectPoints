package com.Firoozeh.ConnectPoints.GameObject;

import java.util.ArrayList;
import java.util.List;

public class AndroidPlayer
{
    List<MyLine> possibleLines;
    List<MyLine> lines;
    MyPoint[][] points;
    public int score;
    public int color;
    private int screenH;
    public long thinkingTime;

    public AndroidPlayer(List<MyLine> lines, MyPoint[][] points, int screenH, int color)
    {
        this.lines = lines;
        this.points = points;
        this.color = color;
        score = 0;
        this.screenH = screenH;
        possibleLines = new ArrayList<MyLine>();
        initializeAllPossibleLines();
        thinkingTime = System.currentTimeMillis();
    }

    public MyLine move()
    {
        MyLine choice = makeDecision();
        lines.add(choice);
        removeFromPossibleLines(choice);
        return choice;
    }

    private MyLine makeDecision()
    {
        MyLine choice = possibleLines.get(0);
        int maxScore = getScore(choice);

        for (MyLine line : possibleLines)
        {
            int tmpScore = getScore(line);
            if (tmpScore > maxScore)
            {
                maxScore = tmpScore;
                choice = line;
            }
        }
        return choice;
    }

    private int getScore(MyLine newLine)
    {
        int score = 0;
        List<MyLine> tmpLines = new ArrayList<MyLine>();
        tmpLines.addAll(lines);
        tmpLines.add(newLine);
        // newLine is vertical or horizontal?
        if (newLine.startPoint.indexI != newLine.stopPoint.indexI)
        {
            // newLine is vertical
            // Get indices of left top point
            int i = Math.min(newLine.startPoint.indexI, newLine.stopPoint.indexI);
            int j = newLine.startPoint.indexJ;
            byte rightCount;
            byte leftCount;

            if ( j > 0 && j < 7)
            {
                rightCount = countLinesAround(i, j, tmpLines);
                j--;
                leftCount = countLinesAround(i, j, tmpLines);
            }
            else if (j == 7)
            {
                rightCount = 1;
                j--;
                leftCount = countLinesAround(i, j, tmpLines);
            }
            else // if (j == 0)
            {
                rightCount = countLinesAround(i, j, tmpLines);
                leftCount = 1;
            }

            if (leftCount + rightCount == 8)
                score += 200;
            else if (leftCount + rightCount == 7)
                score += 150;
            else if (leftCount == 4 || rightCount == 4)
                score += 100;
            else if (leftCount == 3 && rightCount == 3)
                score += -200;
            else if (leftCount == 3 || rightCount == 3)
                score += -100;
            else if (leftCount == 2 || rightCount == 2)
                score += 10;
            else if (leftCount == 1 || rightCount == 1)
                score += 0;

            return score;
        }
        else
        {
            // newLine is horizontal
            // Get indices of left top point
            int i = newLine.startPoint.indexI;
            int j = Math.min(newLine.startPoint.indexJ, newLine.stopPoint.indexJ);
            byte topCount;
            byte bottomCount;

            if ( i > 0 && i < 5)
            {
                bottomCount = countLinesAround(i, j, tmpLines);
                i--;
                topCount = countLinesAround(i, j, tmpLines);
            }
            else if (i == 5)
            {
                bottomCount = 1;
                i--;
                topCount = countLinesAround(i, j, tmpLines);
            }
            else // if (i == 0)
            {
                bottomCount = countLinesAround(i, j, tmpLines);
                topCount = 1;
            }

            if (topCount + bottomCount == 8)
                score += 200;
            else if (topCount + bottomCount == 7)
                score += 150;
            else if (topCount == 4 || bottomCount == 4)
                score += 100;
            else if (topCount == 3 && bottomCount == 3)
                score += -200;
            else if (topCount == 3 || bottomCount == 3)
                score += -100;
            else if (topCount == 2 || bottomCount == 2)
                score += 10;
            else if (topCount == 1 || bottomCount == 1)
                score += 0;

            return score;
        }
    }

    private byte countLinesAround(int leftTopI, int leftTopJ, List<MyLine> tmpLines)
    {
        byte counter = 0;
        for (MyLine line : tmpLines)
        {
            if (points[leftTopI][leftTopJ].isOnLine(line) &&
                points[leftTopI][leftTopJ+1].isOnLine(line))
                counter++;
            else if (points[leftTopI][leftTopJ+1].isOnLine(line) &&
                     points[leftTopI+1][leftTopJ+1].isOnLine(line))
                counter++;
            else if (points[leftTopI+1][leftTopJ+1].isOnLine(line) &&
                     points[leftTopI+1][leftTopJ].isOnLine(line))
                counter++;
            else if (points[leftTopI+1][leftTopJ].isOnLine(line) &&
                     points[leftTopI][leftTopJ].isOnLine(line))
                counter++;
        }

        return counter;
    }

    public void removeFromPossibleLines(MyLine line)
    {
        int index = -1;
        for (MyLine pl : possibleLines)
        {
            if (pl.equals(line))
            {
                index = possibleLines.indexOf(pl);
                break;
            }
        }
        if (index != -1)
        {
            possibleLines.remove(index);
        }
    }

    private void initializeAllPossibleLines()
    {
        for (int i = 0; i < 6; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                // Add vertical lines
                if (i < 5)
                {
                    possibleLines.add(new MyLine(points[i][j], points[i+1][j], color, screenH));
                }
                // Add horizontal lines
                if (j < 7)
                {
                    possibleLines.add(new MyLine(points[i][j], points[i][j+1], color, screenH));
                }
            }
        }
    }
}
