<plannerBenchmark>
  <benchmarkDirectory>local/data/scoreDirectorFactory</benchmarkDirectory>
  <parallelBenchmarkCount>10</parallelBenchmarkCount>
  <warmUpSecondsSpentLimit>60</warmUpSecondsSpentLimit>

  <inheritedSolverBenchmark>
    <subSingleCount>5</subSingleCount>
    <solver>
      <environmentMode>REPRODUCIBLE</environmentMode>
    </solver>
  </inheritedSolverBenchmark>

<#macro terminationDetails>
  <termination>
    <terminationCompositionStyle>OR</terminationCompositionStyle>
    <stepCountLimit>100000</stepCountLimit>
    <unimprovedStepCountLimit>10000</unimprovedStepCountLimit>
    <minutesSpentLimit>10</minutesSpentLimit>
  </termination>
</#macro>

<!--
    Run all the examples on the same local search algorithm.
    We do not care about the final score, we care about score speed.
    Only selector configs are allowed as that is often problem-specific.
-->
<#macro solverDetails benchmarkDescriptor>
  <solutionClass>${benchmarkDescriptor.getSolutionClass()}</solutionClass>
  <#list benchmarkDescriptor.getEntityClasses() as entityClass>
    <entityClass>${entityClass}</entityClass>
  </#list>
  <#if benchmarkDescriptor.getExampleId() == "coachShuttleGathering">
    <localSearch>
      <@terminationDetails />
      <unionMoveSelector>
        <changeMoveSelector>
          <entitySelector>
            <entityClass>org.optaplanner.examples.coachshuttlegathering.domain.BusStop</entityClass>
          </entitySelector>
        </changeMoveSelector>
        <swapMoveSelector>
          <entitySelector>
            <entityClass>org.optaplanner.examples.coachshuttlegathering.domain.BusStop</entityClass>
          </entitySelector>
        </swapMoveSelector>
        <tailChainSwapMoveSelector>
          <entitySelector>
            <entityClass>org.optaplanner.examples.coachshuttlegathering.domain.BusStop</entityClass>
          </entitySelector>
        </tailChainSwapMoveSelector>
        <changeMoveSelector>
          <entitySelector>
            <entityClass>org.optaplanner.examples.coachshuttlegathering.domain.Shuttle</entityClass>
          </entitySelector>
        </changeMoveSelector>
      </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "curriculumCourse">
    <localSearch>
      <@terminationDetails />
      <unionMoveSelector>
        <changeMoveSelector/>
        <swapMoveSelector>
          <filterClass>org.optaplanner.examples.curriculumcourse.solver.move.DifferentCourseSwapMoveFilter</filterClass>
        </swapMoveSelector>
      </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "examination">
    <localSearch>
        <@terminationDetails />
        <unionMoveSelector>
          <cartesianProductMoveSelector>
            <changeMoveSelector>
              <entitySelector id="cartesianProductEntitySelector">
                <entityClass>org.optaplanner.examples.examination.domain.Exam</entityClass>
              </entitySelector>
              <valueSelector variableName="room"/>
            </changeMoveSelector>
            <changeMoveSelector>
              <entitySelector mimicSelectorRef="cartesianProductEntitySelector"/>
              <valueSelector variableName="period">
                <downcastEntityClass>org.optaplanner.examples.examination.domain.LeadingExam</downcastEntityClass>
              </valueSelector>
            </changeMoveSelector>
          </cartesianProductMoveSelector>
          <swapMoveSelector>
            <entitySelector>
              <entityClass>org.optaplanner.examples.examination.domain.LeadingExam</entityClass>
            </entitySelector>
          </swapMoveSelector>
        </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "flightCrewScheduling">
    <localSearch>
      <@terminationDetails />
      <unionMoveSelector>
        <changeMoveSelector/>
        <swapMoveSelector/>
        <pillarChangeMoveSelector>
          <subPillarType>SEQUENCE</subPillarType>
        </pillarChangeMoveSelector>
        <pillarSwapMoveSelector>
          <subPillarType>SEQUENCE</subPillarType>
        </pillarSwapMoveSelector>
      </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "investment">
    <localSearch>
        <@terminationDetails />
        <unionMoveSelector>
          <moveIteratorFactory>
            <moveIteratorFactoryClass>org.optaplanner.examples.investment.solver.move.factory.InvestmentQuantityTransferMoveIteratorFactory</moveIteratorFactoryClass>
          </moveIteratorFactory>
          <moveIteratorFactory>
            <moveIteratorFactoryClass>org.optaplanner.examples.investment.solver.move.factory.InvestmentBiQuantityTransferMoveIteratorFactory</moveIteratorFactoryClass>
          </moveIteratorFactory>
        </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "machineReassignment">
    <localSearch>
        <@terminationDetails />
        <unionMoveSelector>
          <changeMoveSelector/>
          <swapMoveSelector/>
        </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "nQueens">
    <localSearch>
      <@terminationDetails />
      <changeMoveSelector>
        <selectionOrder>ORIGINAL</selectionOrder>
      </changeMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "patientAdmissionSchedule">
    <localSearch>
        <@terminationDetails />
        <unionMoveSelector>
          <changeMoveSelector/>
          <moveListFactory>
            <moveListFactoryClass>org.optaplanner.examples.pas.solver.move.factory.BedDesignationPillarPartSwapMoveFactory</moveListFactoryClass>
          </moveListFactory>
        </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "rockTour">
    <localSearch>
      <@terminationDetails />
      <unionMoveSelector>
        <changeMoveSelector/>
        <swapMoveSelector/>
        <tailChainSwapMoveSelector/>
        <subChainChangeMoveSelector/>
        <subChainSwapMoveSelector/>
      </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "travelingTournament">
    <localSearch>
      <@terminationDetails />
      <unionMoveSelector>
        <swapMoveSelector>
          <cacheType>PHASE</cacheType>
          <selectionOrder>SHUFFLED</selectionOrder>
          <filterClass>org.optaplanner.examples.travelingtournament.solver.move.factory.InverseMatchSwapMoveFilter</filterClass>
        </swapMoveSelector>
        <moveListFactory>
          <cacheType>STEP</cacheType>
          <selectionOrder>SHUFFLED</selectionOrder>
          <moveListFactoryClass>org.optaplanner.examples.travelingtournament.solver.move.factory.MatchChainRotationsMoveFactory</moveListFactoryClass>
        </moveListFactory>
      </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "tsp">
    <localSearch>
        <@terminationDetails />
        <unionMoveSelector>
          <changeMoveSelector>
            <cacheType>STEP</cacheType>
            <selectionOrder>SHUFFLED</selectionOrder>
          </changeMoveSelector>
          <tailChainSwapMoveSelector/>
          <subChainChangeMoveSelector>
            <subChainSelector>
              <maximumSubChainSize>50</maximumSubChainSize>
            </subChainSelector>
            <selectReversingMoveToo>true</selectReversingMoveToo>
          </subChainChangeMoveSelector>
        </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "vehicleRouting">
    <localSearch>
        <@terminationDetails />
        <unionMoveSelector>
          <changeMoveSelector/>
          <swapMoveSelector/>
          <subChainChangeMoveSelector>
            <selectReversingMoveToo>true</selectReversingMoveToo>
          </subChainChangeMoveSelector>
          <subChainSwapMoveSelector>
            <selectReversingMoveToo>true</selectReversingMoveToo>
          </subChainSwapMoveSelector>
        </unionMoveSelector>
    </localSearch>
  <#else>
      <localSearch>
        <@terminationDetails />
      </localSearch>
  </#if>
</#macro>

<#macro problemDetails benchmarkDescriptor>
  <problemBenchmarks>
    <#if benchmarkDescriptor.getSolutionFileIoClass()??>
      <solutionFileIOClass>${benchmarkDescriptor.getSolutionFileIoClass()}</solutionFileIOClass>
    </#if>
    <inputSolutionFile>${benchmarkDescriptor.getInputSolutionFile()}</inputSolutionFile>
    <problemStatisticType>BEST_SCORE</problemStatisticType>
    <problemStatisticType>SCORE_CALCULATION_SPEED</problemStatisticType>
  </problemBenchmarks>
</#macro>

<#list benchmarkDescriptors as benchmarkDescriptor>
  <#if benchmarkDescriptor.getEasyScoreCalculator()??>
    <solverBenchmark>
      <@problemDetails benchmarkDescriptor/>
      <name>${benchmarkDescriptor.getExampleId()} Easy (Java)</name>
      <solver>
        <scoreDirectorFactory>
          <easyScoreCalculatorClass>${benchmarkDescriptor.getEasyScoreCalculator()}</easyScoreCalculatorClass>
        </scoreDirectorFactory>
        <@solverDetails benchmarkDescriptor/>
      </solver>
    </solverBenchmark>
  </#if>
  <#if benchmarkDescriptor.getIncrementalScoreCalculator()??>
    <solverBenchmark>
      <@problemDetails benchmarkDescriptor/>
      <name>${benchmarkDescriptor.getExampleId()} Incremental (Java)</name>
      <solver>
        <scoreDirectorFactory>
          <incrementalScoreCalculatorClass>${benchmarkDescriptor.getIncrementalScoreCalculator()}</incrementalScoreCalculatorClass>
        </scoreDirectorFactory>
        <@solverDetails benchmarkDescriptor/>
      </solver>
    </solverBenchmark>
  </#if>
  <#if benchmarkDescriptor.getConstraintProvider()??>
    <solverBenchmark>
      <@problemDetails benchmarkDescriptor/>
      <name>${benchmarkDescriptor.getExampleId()} Constraint Streams (Bavet)</name>
      <solver>
        <scoreDirectorFactory>
          <constraintStreamImplType>BAVET</constraintStreamImplType>
          <constraintProviderClass>${benchmarkDescriptor.getConstraintProvider()}</constraintProviderClass>
        </scoreDirectorFactory>
        <@solverDetails benchmarkDescriptor/>
      </solver>
    </solverBenchmark>
    <solverBenchmark>
      <@problemDetails benchmarkDescriptor/>
      <name>${benchmarkDescriptor.getExampleId()} Constraint Streams (Drools)</name>
      <solver>
        <scoreDirectorFactory>
          <constraintStreamImplType>DROOLS</constraintStreamImplType>
          <constraintProviderClass>${benchmarkDescriptor.getConstraintProvider()}</constraintProviderClass>
        </scoreDirectorFactory>
        <@solverDetails benchmarkDescriptor/>
      </solver>
    </solverBenchmark>
  </#if>
  <#if benchmarkDescriptor.getDrlFile()??>
    <solverBenchmark>
      <@problemDetails benchmarkDescriptor/>
      <name>${benchmarkDescriptor.getExampleId()} DRL (Drools)</name>
      <solver>
        <scoreDirectorFactory>
          <scoreDrl>${benchmarkDescriptor.getDrlFile()}</scoreDrl>
        </scoreDirectorFactory>
        <@solverDetails benchmarkDescriptor/>
      </solver>
    </solverBenchmark>
  </#if>
</#list>
</plannerBenchmark>
