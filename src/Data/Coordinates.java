package Data;

import java.io.Serializable;

public class Coordinates implements Serializable {
    Coordinates(int x, Double y){
        this.x = x;
        this.y = y;
    }
    private int x;
    private Double y; //Поле не может быть null

    public int getX(){
        return x;
    }
    public Double getY(){
        return y;
    }
    public void updateX(int m){
        x = m;
    }
    public void updateY(Double m){
        y = m;
    }
}