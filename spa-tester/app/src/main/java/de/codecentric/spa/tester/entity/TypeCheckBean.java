package de.codecentric.spa.tester.entity;

import java.util.Date;

import de.codecentric.spa.annotations.Entity;

@Entity
public class TypeCheckBean extends DataEntity {

    private static final long serialVersionUID = 1L;

    public Long aLong;
    public String aString;
    public Double aDouble;
    public Integer aInteger;
    public Float aFloat;
    public Short aShort;
    public Boolean aBoolean;
    public Date date;

    public TypeCheckBean() {
        super();
    }


    @Override
    public String toString() {
        return "TypeCheckBean{" +
                "\n\taLong=" + aLong + "," +
                "\n\taString=" + aString + ',' +
                "\n\taDouble=" + aDouble + "," +
                "\n\taInteger=" + aInteger + "," +
                "\n\taFloat=" + aFloat + "," +
                "\n\taShort=" + aShort + "," +
                "\n\taBoolean=" + aBoolean + "," +
                "\n\tdate=" + date + "," +
                "\n}";
    }
}
