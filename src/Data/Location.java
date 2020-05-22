package Data;

import java.io.Serializable;

public class Location implements Serializable {
    Location(float x, Long y, Long z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    private float x;
    private Long y; //Поле не может быть null
    private Long z; //Поле не может быть null

    public float getX(){
        return x;
    }
    public Long getY(){
        return y;
    }
    public Long getZ(){
        return z;
    }
    public void updateX(float m){
        x = m;
    }
    public void updateY(Long m){
        y = m;
    }
    public void updateZ(Long m){
        z = m;
    }
}
