<plannerBenchmark>
  <benchmarkDirectory>local/data/scoreDirectorFactory</benchmarkDirectory>
  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>

  <inheritedSolverBenchmark>
    <subSingleCount>10</subSingleCount>
    <solver>
      <environmentMode>NON_REPRODUCIBLE</environmentMode>
      <termination>
        <minutesSpentLimit>10</minutesSpentLimit>
      </termination>
    </solver>
  </inheritedSolverBenchmark>

<#macro scoreDirectorDetails exampleId>
  <#if exampleId == "cloudBalancing">
    <initializingScoreTrend>ONLY_DOWN/ONLY_DOWN</initializingScoreTrend>
  <#elseif exampleId == "examination">
    <initializingScoreTrend>ONLY_DOWN</initializingScoreTrend>
  <#elseif exampleId == "investment">
    <initializingScoreTrend>ONLY_DOWN/ANY</initializingScoreTrend>
  <#elseif exampleId == "nQueens">
    <initializingScoreTrend>ONLY_DOWN</initializingScoreTrend>
  <#elseif exampleId == "scrabble">
    <initializingScoreTrend>ONLY_DOWN</initializingScoreTrend>
  <#elseif exampleId == "tsp">
    <initializingScoreTrend>ONLY_DOWN</initializingScoreTrend>
  <#elseif exampleId == "vehicleRouting">
    <initializingScoreTrend>ONLY_DOWN</initializingScoreTrend>
  </#if>
</#macro>

<#macro solverDetails benchmarkDescriptor>
  <solutionClass>${benchmarkDescriptor.getSolutionClass()}</solutionClass>
  <#list benchmarkDescriptor.getEntityClasses() as entityClass>
    <entityClass>${entityClass}</entityClass>
  </#list>
  <#if benchmarkDescriptor.getExampleId() == "conferenceScheduling">
        <constructionHeuristic/>
        <localSearch>
            <localSearchType>TABU_SEARCH</localSearchType>
        </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "curriculumCourse">
        <constructionHeuristic>
          <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
        </constructionHeuristic>
        <localSearch>
          <unionMoveSelector>
            <changeMoveSelector/>
            <swapMoveSelector>
              <filterClass>org.optaplanner.examples.curriculumcourse.solver.move.DifferentCourseSwapMoveFilter</filterClass>
            </swapMoveSelector>
          </unionMoveSelector>
          <acceptor>
            <lateAcceptanceSize>600</lateAcceptanceSize>
          </acceptor>
          <forager>
            <acceptedCountLimit>4</acceptedCountLimit>
          </forager>
        </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "examination">
      <constructionHeuristic>
        <queuedEntityPlacer>
          <entitySelector id="placerEntitySelector">
            <entityClass>org.optaplanner.examples.examination.domain.Exam</entityClass>
            <cacheType>PHASE</cacheType>
            <selectionOrder>SORTED</selectionOrder>
            <sorterManner>DECREASING_DIFFICULTY</sorterManner>
          </entitySelector>
          <cartesianProductMoveSelector>
            <changeMoveSelector>
              <entitySelector mimicSelectorRef="placerEntitySelector"/>
              <valueSelector variableName="period">
                <downcastEntityClass>org.optaplanner.examples.examination.domain.LeadingExam</downcastEntityClass>
                <cacheType>PHASE</cacheType>
              </valueSelector>
            </changeMoveSelector>
            <changeMoveSelector>
              <entitySelector mimicSelectorRef="placerEntitySelector"/>
              <valueSelector variableName="room">
                <cacheType>PHASE</cacheType>
                <selectionOrder>SORTED</selectionOrder>
                <sorterManner>INCREASING_STRENGTH</sorterManner>
              </valueSelector>
            </changeMoveSelector>
          </cartesianProductMoveSelector>
        </queuedEntityPlacer>
      </constructionHeuristic>
      <localSearch>
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
        <acceptor>
          <entityTabuSize>10</entityTabuSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>2000</acceptedCountLimit>
        </forager>
      </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "flightCrewScheduling">
    <constructionHeuristic/>
    <localSearch>
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
      <customPhase>
        <customPhaseCommandClass>org.optaplanner.examples.investment.solver.solution.initializer.InvestmentAllocationSolutionInitializer</customPhaseCommandClass>
      </customPhase>
      <localSearch>
        <unionMoveSelector>
          <moveIteratorFactory>
            <moveIteratorFactoryClass>org.optaplanner.examples.investment.solver.move.factory.InvestmentQuantityTransferMoveIteratorFactory</moveIteratorFactoryClass>
          </moveIteratorFactory>
          <moveIteratorFactory>
            <moveIteratorFactoryClass>org.optaplanner.examples.investment.solver.move.factory.InvestmentBiQuantityTransferMoveIteratorFactory</moveIteratorFactoryClass>
          </moveIteratorFactory>
        </unionMoveSelector>
        <acceptor>
          <lateAcceptanceSize>400</lateAcceptanceSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>1</acceptedCountLimit>
        </forager>
      </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "machineReassignment">
      <customPhase>
        <customPhaseCommandClass>org.optaplanner.examples.machinereassignment.solver.solution.initializer.ToOriginalMachineSolutionInitializer</customPhaseCommandClass>
      </customPhase>
      <localSearch>
        <unionMoveSelector>
          <changeMoveSelector/>
          <swapMoveSelector/>
        </unionMoveSelector>
        <acceptor>
          <entityTabuSize>7</entityTabuSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>2000</acceptedCountLimit>
        </forager>
      </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "nQueens">
    <constructionHeuristic>
      <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
    </constructionHeuristic>
    <localSearch>
      <changeMoveSelector>
        <selectionOrder>ORIGINAL</selectionOrder>
      </changeMoveSelector>
      <acceptor>
        <entityTabuSize>5</entityTabuSize>
      </acceptor>
      <forager>
        <!-- Real world problems require use of <acceptedCountLimit> -->
      </forager>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "patientAdmissionSchedule">
    <constructionHeuristic>
        <constructionHeuristicType>WEAKEST_FIT_DECREASING</constructionHeuristicType>
    </constructionHeuristic>
    <localSearch>
        <unionMoveSelector>
          <changeMoveSelector/>
          <moveListFactory>
            <moveListFactoryClass>org.optaplanner.examples.pas.solver.move.factory.BedDesignationPillarPartSwapMoveFactory</moveListFactoryClass>
          </moveListFactory>
        </unionMoveSelector>
        <acceptor>
          <entityTabuSize>7</entityTabuSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>1000</acceptedCountLimit>
        </forager>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "rockTour">
    <constructionHeuristic/>
    <localSearch>
      <unionMoveSelector>
        <changeMoveSelector/>
        <swapMoveSelector/>
        <tailChainSwapMoveSelector/>
        <subChainChangeMoveSelector/>
        <subChainSwapMoveSelector/>
      </unionMoveSelector>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "travelingTournament">
    <constructionHeuristic/>
    <localSearch>
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
      <acceptor>
        <simulatedAnnealingStartingTemperature>2hard/10000soft</simulatedAnnealingStartingTemperature>
      </acceptor>
      <forager>
        <acceptedCountLimit>4</acceptedCountLimit>
      </forager>
    </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "tsp">
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
      </constructionHeuristic>
      <localSearch>
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
        <acceptor>
          <lateAcceptanceSize>400</lateAcceptanceSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>1</acceptedCountLimit>
        </forager>
      </localSearch>
  <#elseif benchmarkDescriptor.getExampleId() == "vehicleRouting">
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
      </constructionHeuristic>
      <localSearch>
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
        <acceptor>
          <lateAcceptanceSize>200</lateAcceptanceSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>1</acceptedCountLimit>
        </forager>
      </localSearch>
  <#else>
      <constructionHeuristic/>
      <localSearch />
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
          <@scoreDirectorDetails benchmarkDescriptor.getExampleId()/>
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
          <@scoreDirectorDetails benchmarkDescriptor.getExampleId()/>
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
          <@scoreDirectorDetails benchmarkDescriptor.getExampleId()/>
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
          <@scoreDirectorDetails benchmarkDescriptor.getExampleId()/>
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
          <@scoreDirectorDetails benchmarkDescriptor.getExampleId()/>
        </scoreDirectorFactory>
        <@solverDetails benchmarkDescriptor/>
      </solver>
    </solverBenchmark>
  </#if>
</#list>
</plannerBenchmark>
