package ai.timefold.solver.sdb;

import static ai.timefold.solver.sdb.ScoreDirectorType.CONSTRAINT_STREAMS;
import static ai.timefold.solver.sdb.ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED;
import static ai.timefold.solver.sdb.ScoreDirectorType.INCREMENTAL;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import ai.timefold.solver.sdb.problems.CloudBalancingProblem;
import ai.timefold.solver.sdb.problems.ConferenceSchedulingProblem;
import ai.timefold.solver.sdb.problems.CurriculumCourseProblem;
import ai.timefold.solver.sdb.problems.ExaminationProblem;
import ai.timefold.solver.sdb.problems.FlightCrewSchedulingProblem;
import ai.timefold.solver.sdb.problems.MachineReassignmentProblem;
import ai.timefold.solver.sdb.problems.MeetingSchedulingProblem;
import ai.timefold.solver.sdb.problems.NurseRosteringProblem;
import ai.timefold.solver.sdb.problems.PatientAdmissionSchedulingProblem;
import ai.timefold.solver.sdb.problems.Problem;
import ai.timefold.solver.sdb.problems.ProjectJobSchedulingProblem;
import ai.timefold.solver.sdb.problems.TaskAssigningProblem;
import ai.timefold.solver.sdb.problems.TennisProblem;
import ai.timefold.solver.sdb.problems.TravelingTournamentProblem;
import ai.timefold.solver.sdb.problems.TspProblem;
import ai.timefold.solver.sdb.problems.VehicleRoutingProblem;

public enum Example {

    CLOUD_BALANCING(CloudBalancingProblem::new),
    CONFERENCE_SCHEDULING(ConferenceSchedulingProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    CURRICULUM_COURSE(CurriculumCourseProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    EXAMINATION(ExaminationProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    FLIGHT_CREW_SCHEDULING(FlightCrewSchedulingProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    MACHINE_REASSIGNMENT(MachineReassignmentProblem::new, INCREMENTAL, CONSTRAINT_STREAMS,
            CONSTRAINT_STREAMS_JUSTIFIED),
    MEETING_SCHEDULING(MeetingSchedulingProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    NURSE_ROSTERING(NurseRosteringProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    PATIENT_ADMISSION_SCHEDULING(PatientAdmissionSchedulingProblem::new, CONSTRAINT_STREAMS,
            CONSTRAINT_STREAMS_JUSTIFIED),
    PROJECT_JOB_SCHEDULING(ProjectJobSchedulingProblem::new, INCREMENTAL, CONSTRAINT_STREAMS,
            CONSTRAINT_STREAMS_JUSTIFIED),
    TASK_ASSIGNING(TaskAssigningProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    TENNIS(TennisProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    TRAVELING_TOURNAMENT(TravelingTournamentProblem::new, CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED),
    TSP(TspProblem::new),
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
