package tn.demo.project.view;

import tn.demo.project.controller.TimeEstimation;

import java.util.List;
import java.util.UUID;

public record ProjectView(UUID id,
                          String name,
                          String description,
                          boolean isCompleted,
                          String contactPersonEmail,
                          TimeEstimation initialEstimation,
                          List<TaskView> tasks) {

    public TimeEstimation getRemainingEstimation(){
        return initialEstimation.subtract(getCompletedEstimation());
    }
    public TimeEstimation getCompletedEstimation(){
        return tasks.stream()
                .filter(TaskView::isCompleted)
                .map(TaskView::timeEstimation)
                .reduce(TimeEstimation.zeroEstimation(), TimeEstimation::add);
    }

    public ActualTimeSpent getActualTimeSpent(){
        return tasks.stream()
                .filter(TaskView::isCompleted)
                .map(TaskView::actualTimeSpent)
                .reduce(ActualTimeSpent.zero(), ActualTimeSpent::add);
    }

}