package ai.timefold.solver.jmh.scoredirector;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import ai.timefold.solver.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import ai.timefold.solver.examples.tsp.domain.solver.nearby.VisitNearbyDistanceMeter;
import ai.timefold.solver.examples.vehiclerouting.domain.solver.nearby.CustomerNearbyDistanceMeter;
import ai.timefold.solver.jmh.scoredirector.problems.CloudBalancingProblem;
import ai.timefold.solver.jmh.scoredirector.problems.ConferenceSchedulingProblem;
import ai.timefold.solver.jmh.scoredirector.problems.CurriculumCourseProblem;
import ai.timefold.solver.jmh.scoredirector.problems.ExaminationProblem;
import ai.timefold.solver.jmh.scoredirector.problems.FlightCrewSchedulingProblem;
import ai.timefold.solver.jmh.scoredirector.problems.MachineReassignmentProblem;
import ai.timefold.solver.jmh.scoredirector.problems.MeetingSchedulingProblem;
import ai.timefold.solver.jmh.scoredirector.problems.NurseRosteringProblem;
import ai.timefold.solver.jmh.scoredirector.problems.PatientAdmissionSchedulingProblem;
import ai.timefold.solver.jmh.scoredirector.problems.Problem;
import ai.timefold.solver.jmh.scoredirector.problems.ProjectJobSchedulingProblem;
import ai.timefold.solver.jmh.scoredirector.problems.TaskAssigningProblem;
import ai.timefold.solver.jmh.scoredirector.problems.TennisProblem;
import ai.timefold.solver.jmh.scoredirector.problems.TravelingTournamentProblem;
import ai.timefold.solver.jmh.scoredirector.problems.TspProblem;
import ai.timefold.solver.jmh.scoredirector.problems.VehicleRoutingProblem;

public enum Example {

    CLOUD_BALANCING(CloudBalancingProblem::new),
    CONFERENCE_SCHEDULING(ConferenceSchedulingProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    CURRICULUM_COURSE(CurriculumCourseProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    EXAMINATION(ExaminationProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS, ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    FLIGHT_CREW_SCHEDULING(FlightCrewSchedulingProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    MACHINE_REASSIGNMENT(MachineReassignmentProblem::new, ScoreDirectorType.INCREMENTAL, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    MEETING_SCHEDULING(MeetingSchedulingProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    NURSE_ROSTERING(NurseRosteringProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    PATIENT_ADMISSION_SCHEDULING(PatientAdmissionSchedulingProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    PROJECT_JOB_SCHEDULING(ProjectJobSchedulingProblem::new, ScoreDirectorType.INCREMENTAL,
            ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    TASK_ASSIGNING(TaskAssigningProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    TENNIS(TennisProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS, ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    TRAVELING_TOURNAMENT(TravelingTournamentProblem::new, ScoreDirectorType.CONSTRAINT_STREAMS,
            ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED),
    TSP(TspProblem::new, VisitNearbyDistanceMeter.class),
    VEHICLE_ROUTING(VehicleRoutingProblem::new, CustomerNearbyDistanceMeter.class);

    private final Function<ScoreDirectorType, Problem> problemFactory;
    private final Class<? extends NearbyDistanceMeter<?, ?>> nearbyDistanceMeter;
    private final Set<ScoreDirectorType> supportedScoreDirectorTypes;

    Example(Function<ScoreDirectorType, Problem> problemFactory, ScoreDirectorType... supportedScoreDirectorType) {
        this(problemFactory, null, supportedScoreDirectorType);
    }

    Example(Function<ScoreDirectorType, Problem> problemFactory, Class<? extends NearbyDistanceMeter<?, ?>> nearbyDistanceMeter,
            ScoreDirectorType... supportedScoreDirectorType) {
        this.problemFactory = Objects.requireNonNull(problemFactory);
        this.nearbyDistanceMeter = nearbyDistanceMeter;
        if (supportedScoreDirectorType.length == 0) {
            this.supportedScoreDirectorTypes = EnumSet.allOf(ScoreDirectorType.class);
        } else {
            this.supportedScoreDirectorTypes = EnumSet.copyOf(Arrays.asList(supportedScoreDirectorType));
        }
    }

    public Optional<Class<? extends NearbyDistanceMeter<?, ?>>> getNearbyDistanceMeter() {
        return Optional.ofNullable(nearbyDistanceMeter);
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
