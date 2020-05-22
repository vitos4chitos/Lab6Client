package Data;

import java.io.Serializable;

public class Person implements Comparable<Person>, Serializable {
    public Person(Integer id, String name, int cx, Double cy, java.time.LocalDate creationDate, Float height,
                  String passportID, Color hairColor, Country nationality, float lx, Long ly, Long lz){
        this.id = id;
        this.name = name;
        coordinates = new Coordinates(cx, cy);
        this.creationDate = creationDate;
        this.height = height;
        this.passportID = passportID;
        this.hairColor = hairColor;
        this.nationality = nationality;
        location = new Location(lx, ly, lz);
    }
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть
    // уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null,
    // Значение этого поля должно генерироваться автоматически
    private Float height; //Поле не может быть null, Значение поля должно быть больше 0
    private String passportID; //Длина строки не должна быть больше 34,
    // Значение этого поля должно быть уникальным, Поле не может быть null
    private Color hairColor; //Поле не может быть null
    private Country nationality; //Поле не может быть null
    private Location location; //Поле не может быть null

    public Integer getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Coordinates getCoordinates(){
        return  coordinates;
    }
    public java.time.LocalDate getCreationDate(){
        return creationDate;
    }
    public Float getHeight(){
        return height;
    }
    public String getPassportID(){
        return passportID;
    }
    public Color getHairColor(){
        return hairColor;
    }
    public Country getNationality(){
        return nationality;
    }
    public Location getLocation(){
        return location;
    }
    public void updateName(String m){
        name = m;
    }
    public void updateCoordinates(int x, Double y){
        coordinates.updateX(x);
        coordinates.updateY(y);
    }
    public void updateCreationDate(java.time.LocalDate x){
        creationDate = x;
    }
    public void updateHeight(Float x){
        height = x;
    }
    public void updatePassportId(String x){
        passportID = x;
    }
    public void updateHairColor(Color x){
        hairColor = x;
    }
    public void updateNationality(Country x){
        nationality = x;
    }
    public void updateLocation(float x, Long y, Long z){
        location.updateX(x);
        location.updateY(y);
        location.updateZ(z);
    }

    @Override
    public int compareTo(Person o) {
        return name.compareTo(o.getName());
    }

    public void changeId(Integer id){
        this.id = id;
    }
    public void changeCreationTime(java.time.LocalDate date){
        creationDate = date;
    }
}