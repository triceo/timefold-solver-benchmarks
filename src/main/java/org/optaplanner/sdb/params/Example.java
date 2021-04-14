package org.optaplanner.sdb.params;

import org.optaplanner.sdb.problems.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static org.optaplanner.sdb.params.ScoreDirector.*;

public enum Example {

    CLOUD_BALANCING(CloudBalancingProblem::new,
            JAVA_EASY, JAVA_INCREMENTAL, CONSTRAINT_STREAMS_DROOLS, DRL),
    COACH_SHUTTLE_GATHERING(CoachShuttleGatheringProblem::new,
            JAVA_EASY, CONSTRAINT_STREAMS_DROOLS, DRL),
    CONFERENCE_SCHEDULING(ConferenceSchedulingProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    CURRICULUM_COURSE(CurriculumCourseProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    EXAMINATION(ExaminationProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    FLIGHT_CREW_SCHEDULING(FlightCrewSchedulingProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    INVESTMENT(InvestmentProblem::new,
            JAVA_EASY, JAVA_INCREMENTAL, CONSTRAINT_STREAMS_DROOLS, DRL),
    MACHINE_REASSIGNMENT(MachineReassignmentProblem::new,
            JAVA_INCREMENTAL, CONSTRAINT_STREAMS_DROOLS, DRL),
    MEETING_SCHEDULING(MeetingSchedulingProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    NQUEENS(NQueensProblem::new),
    PATIENT_ADMISSION_SCHEDULING(PatientAdmissionSchedulingProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    ROCK_TOUR(RockTourProblem::new,
            CONSTRAINT_STREAMS_BAVET, CONSTRAINT_STREAMS_DROOLS, DRL),
    TASK_ASSIGNING(TaskAssigningProblem::new,
            CONSTRAINT_STREAMS_BAVET, CONSTRAINT_STREAMS_DROOLS, DRL),
    TENNIS(TennisProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    TRAVELING_TOURNAMENT(TravelingTournamentProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    TSP(TspProblem::new,
            JAVA_EASY, JAVA_INCREMENTAL, CONSTRAINT_STREAMS_DROOLS, DRL),
    VEHICLE_ROUTING(VehicleRoutingProblem::new);

    private final Function<ScoreDirector, Problem> problemFactory;
    private final Set<ScoreDirector> supportedScoreDirectors;

    Example(Function<ScoreDirector, Problem> problemFactory, ScoreDirector... supportedScoreDirector) {
        this.problemFactory = Objects.requireNonNull(problemFactory);
        if (supportedScoreDirector.length == 0) {
            this.supportedScoreDirectors = EnumSet.allOf(ScoreDirector.class);
        } else {
            this.supportedScoreDirectors = EnumSet.copyOf(Arrays.asList(supportedScoreDirector));
        }
    }

    public boolean isSupportedOn(ScoreDirector scoreDirector) {
        return supportedScoreDirectors.contains(scoreDirector);
    }

    public Problem create(ScoreDirector scoreDirector) {
        if (!isSupportedOn(scoreDirector)) {
            throw new IllegalArgumentException("Unsupported score director (" + scoreDirector + ") for example ("
                    + this + ").");
        }
        return problemFactory.apply(scoreDirector);
    }

}
