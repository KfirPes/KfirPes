package dev.tomco.a24b_11345a_l02_03.Logic;

public class GameManager {

    private static final int ANSWER_POINTS = 10;
    private int score = 0;
    private int wrongAnswers = 0;
    private int lifeCount;
    private int numBody=1;
    private int rowObs;
    private int rowCoin;
    private int colCoin;
    private int colObs;
    private int status=0;
    public GameManager() {
        this(3);
        // lifeCount = 3;
        // allCountries = DataManager.getCountries();
    }

    public GameManager(int lifeCount) {
        this.lifeCount = lifeCount;
    }

    public int getScore() {
        return score;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public int getLifeCount() {
        return lifeCount;
    }



    public boolean isGameLost(){
        return getLifeCount() == getWrongAnswers();
    }

    public void setStatus(){
        this.status++;
    }
    public int getStatus(){
        return this.status;
    }
    public void setColObs(int col)
    {
        this.colObs=col;
        this.rowObs=0;
    }
    public void setRowObs(){
        rowObs++;
        if (rowObs==17)
            if (numBody==colObs) {
                wrongAnswers++;
            }
    }
    public void setColCoin(int col)
    {
        this.colCoin=col;
        this.rowCoin=0;
    }
    public void setRowCoin(){
        rowCoin++;
        if (rowCoin==17)
            if (numBody==colCoin) {
                score += ANSWER_POINTS;
            }


    }
    public  void setWrongAnswers(int num){
        this.wrongAnswers=0;
    }

    public void setNumBody(int where)
    {
        numBody=where;
    }
    public void setNumBody(boolean where){
        if (numBody<4&&where==true)
            numBody++;
        if (numBody>0&&where==false)
            numBody--;
    }
    public int getNumBody(){
        return numBody;
    }
    public int getRowObs(){
        return rowObs;
    }
    public int getColObs(){
        return colObs;
    }
    public int getRowCoin(){
        return rowCoin;
    }
    public int getColCoin(){
        return colCoin;
    }
}