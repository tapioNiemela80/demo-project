package tn.demo.team.controller;

public record ActualSpentTime(int hours, int minutes) {

    public ActualSpentTime add(ActualSpentTime other){
        return new ActualSpentTime(hours + other.hours(), minutes + other.minutes());
    }

}
