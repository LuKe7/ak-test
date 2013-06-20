package com.example.brightcovetest;

/**
 * Created by dx068 on 13-06-18.
 */
public class Option {

    public static class Type {
        public static final int text = 0;
        public static final int slider = 1;

    }

    String name;
    int type;
    int optId;

    public Option(String n , int t, int optId){
        name = n;
        type = t;
        this.optId =optId;
    }

    public int getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return optId;
    }

    public void setName(String name){
        this.name = name;
    }




}
