package org.optaplanner.examples.app.params;

import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.app.problems.*;

import java.util.Objects;
import java.util.function.Function;

public enum Example {

    CLOUD_BALANCING(CloudBalancingProblem::new),
    COACH_SHUTTLE_GATHERING(CoachShuttleGatheringProblem::new),
    CONFERENCE_SCHEDULING(ConferenceSchedulingProblem::new),
    CURRICULUM_COURSE(CurriculumCourseProblem::new),
    EXAMINATION(ExaminationProblem::new),
    FLIGHT_CREW_SCHEDULING(FlightCrewSchedulingProblem::new),
    INVESTMENT(InvestmentProblem::new),
    MACHINE_REASSIGNMENT(MachineReassignmentProblem::new),
    MEETING_SCHEDULING(MeetingSchedulingProblem::new),
    NQUEENS(NQueensProblem::new),
    PATIENT_ADMISSION_SCHEDULING(PatientAdmissionSchedulingProblem::new),
    ROCK_TOUR(RockTourProblem::new),
    TASK_ASSIGNING(TaskAssigningProblem::new),
    TENNIS(TennisProblem::new),
    TRAVELING_TOURNAMENT(TravelingTournamentProblem::new),
    TSP(TspProblem::new),
    VEHICLE_ROUTING(VehicleRoutingProblem::new);

    private final Function<ScoreDirector, Problem> problemFactory;

    Example(Function<ScoreDirector, Problem> problemFactory) {
        this.problemFactory = Objects.requireNonNull(problemFactory);
    }

    public Problem create(ScoreDirector scoreDirector) {
        return problemFactory.apply(scoreDirector);
    }

}
