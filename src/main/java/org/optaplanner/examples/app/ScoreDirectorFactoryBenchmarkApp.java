/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingConstraintProvider;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingIncrementalScoreCalculator;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingMapBasedEasyScoreCalculator;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalanceXmlSolutionFileIO;
import org.optaplanner.examples.coachshuttlegathering.domain.BusOrStop;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;
import org.optaplanner.examples.coachshuttlegathering.persistence.CoachShuttleGatheringXmlSolutionFileIO;
import org.optaplanner.examples.coachshuttlegathering.solver.CoachShuttleGatheringConstraintProvider;
import org.optaplanner.examples.coachshuttlegathering.solver.CoachShuttleGatheringEasyScoreCalculator;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.optional.score.ConferenceSchedulingConstraintProvider;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.optional.score.CurriculumCourseConstraintProvider;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseXmlSolutionFileIO;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.domain.FollowingExam;
import org.optaplanner.examples.examination.domain.LeadingExam;
import org.optaplanner.examples.examination.persistence.ExaminationXmlSolutionFileIO;
import org.optaplanner.examples.examination.solver.score.ExaminationConstraintProvider;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.optional.score.FlightCrewSchedulingConstraintProvider;
import org.optaplanner.examples.flightcrewscheduling.persistence.FlightCrewSchedulingXlsxFileIO;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.optional.score.InvestmentConstraintProvider;
import org.optaplanner.examples.investment.persistence.InvestmentXmlSolutionFileIO;
import org.optaplanner.examples.investment.solver.score.InvestmentEasyScoreCalculator;
import org.optaplanner.examples.investment.solver.score.InvestmentIncrementalScoreCalculator;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.optional.score.MeetingSchedulingConstraintProvider;
import org.optaplanner.examples.meetingscheduling.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.persistence.NQueensXmlSolutionFileIO;
import org.optaplanner.examples.nqueens.solver.score.NQueensAdvancedIncrementalScoreCalculator;
import org.optaplanner.examples.nqueens.solver.score.NQueensConstraintProvider;
import org.optaplanner.examples.nqueens.solver.score.NQueensMapBasedEasyScoreCalculator;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleXmlSolutionFileIO;
import org.optaplanner.examples.pas.solver.score.PatientAdmissionScheduleConstraintProvider;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockStandstill;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;
import org.optaplanner.examples.rocktour.optional.score.RockTourConstraintProvider;
import org.optaplanner.examples.rocktour.persistence.RockTourXlsxFileIO;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskOrEmployee;
import org.optaplanner.examples.taskassigning.persistence.TaskAssigningXmlSolutionFileIO;
import org.optaplanner.examples.taskassigning.solver.score.TaskAssigningConstraintProvider;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.optional.score.TennisConstraintProvider;
import org.optaplanner.examples.tennis.persistence.TennisXmlSolutionFileIO;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.optional.score.TravelingTournamentConstraintProvider;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentXmlSolutionFileIO;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.optional.score.TspConstraintProvider;
import org.optaplanner.examples.tsp.persistence.TspFileIO;
import org.optaplanner.examples.tsp.solver.score.TspEasyScoreCalculator;
import org.optaplanner.examples.tsp.solver.score.TspIncrementalScoreCalculator;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingConstraintProvider;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingFileIO;
import org.optaplanner.examples.vehiclerouting.solver.score.VehicleRoutingEasyScoreCalculator;
import org.optaplanner.examples.vehiclerouting.solver.score.VehicleRoutingIncrementalScoreCalculator;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

// TODO include cheaptime
// TODO include dinnerparty
// TODO include nurserostering
// TODO include projectjobscheduling
public class ScoreDirectorFactoryBenchmarkApp {

    public static void main(String... args) {
        ProblemDescriptor[] descriptors = {
                new ProblemDescriptor("cloudBalancing", CloudBalance.class, CloudBalanceXmlSolutionFileIO.class,
                        "unsolved/1600computers-4800processes.xml", CloudBalancingMapBasedEasyScoreCalculator.class,
                        CloudBalancingIncrementalScoreCalculator.class, CloudBalancingConstraintProvider.class,
                        CloudProcess.class),
                new ProblemDescriptor("coachShuttleGathering", CoachShuttleGatheringSolution.class, CoachShuttleGatheringXmlSolutionFileIO.class,
                        "unsolved/demo01.xml", CoachShuttleGatheringEasyScoreCalculator.class, null,
                        CoachShuttleGatheringConstraintProvider.class, Coach.class, Shuttle.class, BusStop.class, StopOrHub.class, BusOrStop.class),
                new ProblemDescriptor("conferenceScheduling", ConferenceSolution.class, ConferenceSchedulingXlsxFileIO.class,
                        "unsolved/216talks-18timeslots-20rooms.xlsx", null, null,
                        ConferenceSchedulingConstraintProvider.class, Talk.class),
                new ProblemDescriptor("curriculumCourse", CourseSchedule.class, CurriculumCourseXmlSolutionFileIO.class,
                        "unsolved/comp07.xml", null, null, CurriculumCourseConstraintProvider.class, Lecture.class),
                new ProblemDescriptor("examination", Examination.class, ExaminationXmlSolutionFileIO.class,
                        "unsolved/exam_comp_set3.xml", null, null, ExaminationConstraintProvider.class, Exam.class,
                        LeadingExam.class, FollowingExam.class),
                new ProblemDescriptor("flightCrewScheduling", FlightCrewSolution.class, FlightCrewSchedulingXlsxFileIO.class,
                        "unsolved/875flights-7days-Europe.xlsx", null, null,
                        FlightCrewSchedulingConstraintProvider.class, FlightAssignment.class, Employee.class),
                new ProblemDescriptor("investment", InvestmentSolution.class, InvestmentXmlSolutionFileIO.class,
                        "unsolved/de_smet_1.xml", InvestmentEasyScoreCalculator.class,
                        InvestmentIncrementalScoreCalculator.class, InvestmentConstraintProvider.class,
                        AssetClassAllocation.class),
                /*
                new ProblemDescriptor("machineReassignment", MachineReassignment.class, MachineReassignmentFileIO.class,
                        "import/model_b_9.txt", null, MachineReassignmentIncrementalScoreCalculator.class,
                        MachineReassignmentConstraintProvider.class, MrProcessAssignment.class),

                 */
                new ProblemDescriptor("meetingScheduling", MeetingSchedule.class, MeetingSchedulingXlsxFileIO.class,
                        "unsolved/400meetings-1280timegrains-5rooms.xlsx", null, null,
                        MeetingSchedulingConstraintProvider.class, MeetingAssignment.class),
                new ProblemDescriptor("nQueens", NQueens.class, NQueensXmlSolutionFileIO.class, "unsolved/256queens.xml",
                        NQueensMapBasedEasyScoreCalculator.class, NQueensAdvancedIncrementalScoreCalculator.class,
                        NQueensConstraintProvider.class, Queen.class),
                new ProblemDescriptor("patientAdmissionSchedule", PatientAdmissionSchedule.class,
                        PatientAdmissionScheduleXmlSolutionFileIO.class, "unsolved/testdata12.xml", null, null,
                        PatientAdmissionScheduleConstraintProvider.class, BedDesignation.class),
                new ProblemDescriptor("rockTour", RockTourSolution.class, RockTourXlsxFileIO.class, "unsolved/47shows.xlsx",
                        null, null, RockTourConstraintProvider.class, RockShow.class, RockStandstill.class),
                new ProblemDescriptor("taskAssigning", TaskAssigningSolution.class, TaskAssigningXmlSolutionFileIO.class,
                        "unsolved/500tasks-20employees.xml", null, null, TaskAssigningConstraintProvider.class,
                        TaskOrEmployee.class, Task.class),
                new ProblemDescriptor("tennis", TennisSolution.class, TennisXmlSolutionFileIO.class,
                        "unsolved/munich-7teams.xml", null, null, TennisConstraintProvider.class,
                        TeamAssignment.class),
                new ProblemDescriptor("travelingTournament", TravelingTournament.class,
                        TravelingTournamentXmlSolutionFileIO.class,
                        "unsolved/1-nl14.xml", null, null, TravelingTournamentConstraintProvider.class, Match.class),
                new ProblemDescriptor("tsp", TspSolution.class, TspFileIO.class, "import/cook/air/lu980.tsp",
                        TspEasyScoreCalculator.class, TspIncrementalScoreCalculator.class, TspConstraintProvider.class,
                        Visit.class),
                new ProblemDescriptor("vehicleRouting", VehicleRoutingSolution.class, VehicleRoutingFileIO.class,
                        "import/belgium/basic/air/belgium-n2750-k55.vrp", VehicleRoutingEasyScoreCalculator.class,
                        VehicleRoutingIncrementalScoreCalculator.class, VehicleRoutingConstraintProvider.class,
                        Standstill.class, Customer.class, TimeWindowedCustomer.class)
        };
        Map<String, Object> model = new HashMap<>();
        ProblemDescriptor[] enabledProblems = Arrays.stream(descriptors) // All problems are enabled.
                .toArray(ProblemDescriptor[]::new);
        model.put("benchmarkDescriptors", enabledProblems);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(
                "org/optaplanner/examples/app/benchmark/scoreDirectorFactoryBenchmarkConfigTemplate.xml.ftl", model);
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
        benchmark.benchmarkAndShowReportInBrowser();
    }

    public static final class ProblemDescriptor {

        private final String exampleId;
        private final String solutionFileIoClass;
        private final String inputSolutionFile;
        private final String drlFile;
        private final String easyScoreCalculator;
        private final String incrementalScoreCalculator;
        private final String constraintProvider;
        private final String solutionClass;
        private final Set<String> entityClasses;

        public <Solution_, Score_ extends Score<Score_>> ProblemDescriptor(String exampleId,
                Class<Solution_> solutionClass, Class<? extends SolutionFileIO<Solution_>> solutionFileIoClass,
                String inputSolutionFile,
                Class<? extends EasyScoreCalculator<Solution_, Score_>> easyScoreCalculatorClass,
                Class<? extends IncrementalScoreCalculator<Solution_, Score_>> incrementalScoreCalculatorClass,
                Class<? extends ConstraintProvider> constraintProviderClass,
                Class<?>... entityClasses) {
            this.exampleId = exampleId;
            this.solutionFileIoClass = solutionFileIoClass == null ? null : solutionFileIoClass.getCanonicalName();
            String parentFolder = exampleId.equals("patientAdmissionSchedule") ? "pas" : exampleId.toLowerCase();
            String fullInputSolutionPath = "data/" + parentFolder + "/" + inputSolutionFile;
            if (!new File(fullInputSolutionPath).exists()) {
                throw new IllegalArgumentException(
                        "No input solution (" + inputSolutionFile + ") for example (" + exampleId + ").");
            }
            this.inputSolutionFile = fullInputSolutionPath;
            String fullDrlPath = "org/optaplanner/examples/" + parentFolder + "/solver/" + exampleId + "Constraints.drl";
            try (InputStream stream = getClass().getResourceAsStream("/" + fullDrlPath)) {
                if (stream == null) {
                    throw new IllegalArgumentException("No DRL (" + fullDrlPath + ") for example (" + exampleId + ").");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("No DRL (" + fullDrlPath + ") for example (" + exampleId + ").", e);
            }
            this.drlFile = fullDrlPath;
            this.easyScoreCalculator = easyScoreCalculatorClass == null ? null : easyScoreCalculatorClass.getCanonicalName();
            this.incrementalScoreCalculator = incrementalScoreCalculatorClass == null ? null
                    : incrementalScoreCalculatorClass.getCanonicalName();
            this.constraintProvider = constraintProviderClass == null ? null : constraintProviderClass.getCanonicalName();
            this.solutionClass = solutionClass.getCanonicalName();
            this.entityClasses = Stream.of(entityClasses)
                    .map(Class::getCanonicalName)
                    .collect(Collectors.toSet());
        }

        public String getExampleId() {
            return exampleId;
        }

        public String getSolutionFileIoClass() {
            return solutionFileIoClass;
        }

        public String getInputSolutionFile() {
            return inputSolutionFile;
        }

        public String getDrlFile() {
            return drlFile;
        }

        public String getEasyScoreCalculator() {
            return easyScoreCalculator;
        }

        public String getIncrementalScoreCalculator() {
            return incrementalScoreCalculator;
        }

        public String getConstraintProvider() {
            return constraintProvider;
        }

        public String getSolutionClass() {
            return solutionClass;
        }

        public Set<String> getEntityClasses() {
            return entityClasses;
        }
    }
}
