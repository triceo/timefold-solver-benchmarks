package org.optaplanner.sdb.params;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.optaplanner.sdb.problems.CloudBalancingProblem;
import org.optaplanner.sdb.problems.CoachShuttleGatheringProblem;
import org.optaplanner.sdb.problems.ConferenceSchedulingProblem;
import org.optaplanner.sdb.problems.CurriculumCourseProblem;
import org.optaplanner.sdb.problems.ExaminationProblem;
import org.optaplanner.sdb.problems.FlightCrewSchedulingProblem;
import org.optaplanner.sdb.problems.InvestmentProblem;
import org.optaplanner.sdb.problems.MachineReassignmentProblem;
import org.optaplanner.sdb.problems.MeetingSchedulingProblem;
import org.optaplanner.sdb.problems.NQueensProblem;
import org.optaplanner.sdb.problems.NurseRosteringProblem;
import org.optaplanner.sdb.problems.PatientAdmissionSchedulingProblem;
import org.optaplanner.sdb.problems.Problem;
import org.optaplanner.sdb.problems.ProjectJobSchedulingProblem;
import org.optaplanner.sdb.problems.RockTourProblem;
import org.optaplanner.sdb.problems.TaskAssigningProblem;
import org.optaplanner.sdb.problems.TennisProblem;
import org.optaplanner.sdb.problems.TravelingTournamentProblem;
import org.optaplanner.sdb.problems.TspProblem;
import org.optaplanner.sdb.problems.VehicleRoutingProblem;

import static org.optaplanner.sdb.params.ScoreDirectorType.CONSTRAINT_STREAMS_BAVET;
import static org.optaplanner.sdb.params.ScoreDirectorType.CONSTRAINT_STREAMS_DROOLS;
import static org.optaplanner.sdb.params.ScoreDirectorType.DRL;
import static org.optaplanner.sdb.params.ScoreDirectorType.JAVA_EASY;
import static org.optaplanner.sdb.params.ScoreDirectorType.JAVA_INCREMENTAL;

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
    NURSE_ROSTERING(NurseRosteringProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    PATIENT_ADMISSION_SCHEDULING(PatientAdmissionSchedulingProblem::new,
            CONSTRAINT_STREAMS_DROOLS, DRL),
    PROJECT_JOB_SCHEDULING(ProjectJobSchedulingProblem::new,
            JAVA_INCREMENTAL, CONSTRAINT_STREAMS_DROOLS, DRL),
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

    private final Function<ScoreDirectorType, Problem> problemFactory;
    private final Set<ScoreDirectorType> supportedScoreDirectorTypes;

    Example(Function<ScoreDirectorType, Problem> problemFactory, ScoreDirectorType... supportedScoreDirectorType) {
        this.problemFactory = Objects.requireNonNull(problemFactory);
        if (supportedScoreDirectorType.length == 0) {
            this.supportedScoreDirectorTypes = EnumSet.allOf(ScoreDirectorType.class);
        } else {
            this.supportedScoreDirectorTypes = EnumSet.copyOf(Arrays.asList(supportedScoreDirectorType));
        }
    }

    public boolean isSupportedOn(ScoreDirectorType scoreDirectorType) {
        return supportedScoreDirectorTypes.contains(scoreDirectorType);
    }

    public Problem create(ScoreDirectorType scoreDirectorType) {
        if (!isSupportedOn(scoreDirectorType)) {
            throw new IllegalArgumentException("Unsupported score director (" + scoreDirectorType + ") for example ("
                    + this + ").");
        }
        return problemFactory.apply(scoreDirectorType);
    }

}
