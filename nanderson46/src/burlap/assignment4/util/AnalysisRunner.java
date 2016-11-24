package burlap.assignment4.util;

import burlap.assignment4.BasicGridWorld;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class AnalysisRunner {

	final SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();

	private int MAX_ITERATIONS;
	private int NUM_INTERVALS;

	public AnalysisRunner(int MAX_ITERATIONS, int NUM_INTERVALS) {
		this.MAX_ITERATIONS = MAX_ITERATIONS;
		this.NUM_INTERVALS = NUM_INTERVALS;
		
		int increment = MAX_ITERATIONS/NUM_INTERVALS;
		for(int numIterations = increment;numIterations<=MAX_ITERATIONS;numIterations+=increment ){
			AnalysisAggregator.addNumberOfIterations(numIterations);

		}

	}
	public void runValueIteration(BasicGridWorld gen, Domain domain,
			State initialState, RewardFunction rf, TerminalFunction tf, boolean showPolicyMap) {
		System.out.println("//Value Iteration Analysis//");
		ValueIteration vi = null;
		Policy p = null;
		EpisodeAnalysis ea = null;
		int increment = MAX_ITERATIONS/NUM_INTERVALS;
		for(int numIterations = increment;numIterations<=MAX_ITERATIONS;numIterations+=increment ){
			long startTime = System.nanoTime();
			vi = new ValueIteration(
					domain,
					rf,
					tf,
					0.99,
					hashingFactory,
					-1, numIterations); //Added a very high delta number in order to guarantee that value iteration occurs the max number of iterations
										   //for comparison with the other algorithms.
	
			// run planning from our initial state
			p = vi.planFromState(initialState);
			AnalysisAggregator.addMillisecondsToFinishValueIteration((int) (System.nanoTime()-startTime)/1000000);

			// evaluate the policy with one roll out visualize the trajectory
			ea = p.evaluateBehavior(initialState, rf, tf);
			AnalysisAggregator.addValueIterationReward(calcRewardInEpisode(ea));
			AnalysisAggregator.addStepsToFinishValueIteration(ea.numTimeSteps());
		}
		
//		Visualizer v = gen.getVisualizer();
//		new EpisodeSequenceVisualizer(v, domain, Arrays.asList(ea));
		MapPrinter.printPolicyMap(vi.getAllStates(), p, gen.getMap());
		System.out.println("\n\n");
		if(showPolicyMap){
			simpleValueFunctionVis((ValueFunction)vi, p, initialState, domain, hashingFactory);
		}
	}

	public void runPolicyIteration(BasicGridWorld gen, Domain domain,
			State initialState, RewardFunction rf, TerminalFunction tf, boolean showPolicyMap) {
		System.out.println("//Policy Iteration Analysis//");
		PolicyIteration pi = null;
		Policy p = null;
		EpisodeAnalysis ea = null;
		int increment = MAX_ITERATIONS/NUM_INTERVALS;
		for(int numIterations = increment;numIterations<=MAX_ITERATIONS;numIterations+=increment ){
			long startTime = System.nanoTime();
			pi = new PolicyIteration(
					domain,
					rf,
					tf,
					0.99,
					hashingFactory,
					-1, 1, numIterations);
	
			// run planning from our initial state
			p = pi.planFromState(initialState);
			AnalysisAggregator.addMillisecondsToFinishPolicyIteration((int) (System.nanoTime()-startTime)/1000000);

			// evaluate the policy with one roll out visualize the trajectory
			ea = p.evaluateBehavior(initialState, rf, tf);
			AnalysisAggregator.addPolicyIterationReward(calcRewardInEpisode(ea));
			AnalysisAggregator.addStepsToFinishPolicyIteration(ea.numTimeSteps());
		}

//		Visualizer v = gen.getVisualizer();
//		new EpisodeSequenceVisualizer(v, domain, Arrays.asList(ea));


		MapPrinter.printPolicyMap(getAllStates(domain,rf,tf,initialState), p, gen.getMap());
		System.out.println("\n\n");

		//visualize the value function and policy.
		if(showPolicyMap){
			simpleValueFunctionVis(pi, p, initialState, domain, hashingFactory);
		}
	}

	public void simpleValueFunctionVis(ValueFunction valueFunction, Policy p, 
			State initialState, Domain domain, HashableStateFactory hashingFactory){

		List<State> allStates = StateReachability.getReachableStates(initialState,
				(SADomain)domain, hashingFactory);
		ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(
				allStates, valueFunction, p);
		gui.initGUI();

	}
	
	public void runQLearning(BasicGridWorld gen, Domain domain,
			State initialState, RewardFunction rf, TerminalFunction tf,
			SimulatedEnvironment env, boolean showPolicyMap) throws FileNotFoundException {
		System.out.println("//Q Learning Analysis//");

		QLearning agent = null;
		Policy p = null;
		EpisodeAnalysis ea = null;
		int increment = MAX_ITERATIONS/NUM_INTERVALS;
		String dir = "c:/bench/ML-Reinforcement/data/";
		PrintWriter pw = new PrintWriter(new File(dir+"QTrainData.csv"));
        pw.write("Learning Rate, Gamma, Reward \n");



		for(double  lr = .1; lr<=1; lr+=.05) {
		for(double gamma = .1; gamma<=1; gamma+=.05){
		for(int numIterations = increment;numIterations<=MAX_ITERATIONS;numIterations+=increment ) {
			long startTime = System.nanoTime();
            StringBuilder sb = new StringBuilder();
			agent = new QLearning(
					domain,
					gamma,
					hashingFactory,
					0.99, lr);

			for (int i = 0; i < numIterations; i++) {
				ea = agent.runLearningEpisode(env);
				env.resetEnvironment();
			}
			agent.initializeForPlanning(rf, tf, 1);
			p = agent.planFromState(initialState);
            sb.append(lr);
            sb.append(", ");
            sb.append(gamma);
            sb.append(", ");
            sb.append(calcRewardInEpisode(ea));
            sb.append("\n");
            pw.write(sb.toString());
            System.out.println(lr + " " + gamma);
			AnalysisAggregator.addQLearningReward(calcRewardInEpisode(ea));
			AnalysisAggregator.addMillisecondsToFinishQLearning((int) (System.nanoTime() - startTime) / 1000000);
			AnalysisAggregator.addStepsToFinishQLearning(ea.numTimeSteps());
		}}
		}
        pw.close();
		MapPrinter.printPolicyMap(getAllStates(domain,rf,tf,initialState), p, gen.getMap());
		System.out.println("\n\n");

		//visualize the value function and policy.
		if(showPolicyMap){
			simpleValueFunctionVis((ValueFunction)agent, p, initialState, domain, hashingFactory);
		}

	}
	
	private static List<State> getAllStates(Domain domain,
			 RewardFunction rf, TerminalFunction tf,State initialState){
		ValueIteration vi = new ValueIteration(
				domain,
				rf,
				tf,
				0.99,
				new SimpleHashableStateFactory(),
				.5, 100);
		vi.planFromState(initialState);

		return vi.getAllStates();
	}
	
	public double calcRewardInEpisode(EpisodeAnalysis ea) {
		double myRewards = 0;

		//sum all rewards
		for (int i = 0; i<ea.rewardSequence.size(); i++) {
			myRewards += ea.rewardSequence.get(i);
		}
		return myRewards;
	}
	
}
