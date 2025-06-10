package tn.demo.project.controller;

import tn.demo.team.controller.ActualSpentTime;

import java.util.List;
import java.util.UUID;

public record ProjectViewDto(UUID id,
                             String name,
                             String description,
                             boolean isCompleted,
                             String contactPersonEmail,
                             TimeEstimation initialEstimation,
                             List<TaskViewDto> tasks) {

    public TimeEstimation getRemainingEstimation(){
        return initialEstimation.subtract(getCompletedEstimation());
    }
    public TimeEstimation getCompletedEstimation(){
        return tasks.stream()
                .filter(TaskViewDto::isCompleted)
                .map(TaskViewDto::timeEstimation)
                .reduce(TimeEstimation.zeroEstimation(), TimeEstimation::add);
    }

    public ActualSpentTime getActualTimeSpent(){
        return tasks.stream()
                .map(TaskViewDto::actualSpentTime)
                .reduce(ActualSpentTime.zero(), ActualSpentTime::add);
    }

}