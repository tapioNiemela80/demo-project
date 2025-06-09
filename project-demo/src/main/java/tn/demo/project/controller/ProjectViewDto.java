package tn.demo.project.controller;

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
        if(tasks.isEmpty()){
            return initialEstimation;
        }
        TimeEstimation completedEstimation = getCompletedEstimation();
        return initialEstimation.subtract(completedEstimation);
    }
    public TimeEstimation getCompletedEstimation(){
        return tasks.stream()
                .filter(TaskViewDto::isCompleted)
                .map(TaskViewDto::timeEstimation)
                .reduce(TimeEstimation.zeroEstimation(), TimeEstimation::add);
    }

}