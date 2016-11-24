package burlap.assignment4.util;

import java.util.ArrayList;
import java.util.List;
import java.io.PrintWriter;

public final class AnalysisAggregator {
	private static List<Integer> numIterations = new ArrayList<Integer>();
	private static List<Integer> stepsToFinishValueIteration = new ArrayList<Integer>();
	private static List<Integer> stepsToFinishPolicyIteration = new ArrayList<Integer>();
	private static List<Integer> stepsToFinishQLearning = new ArrayList<Integer>();
	
	private static List<Integer> millisecondsToFinishValueIteration = new ArrayList<Integer>();
	private static List<Integer> millisecondsToFinishPolicyIteration = new ArrayList<Integer>();
	private static List<Integer> millisecondsToFinishQLearning = new ArrayList<Integer>();

	private static List<Double> rewardsForValueIteration = new ArrayList<Double>();
	private static List<Double> rewardsForPolicyIteration = new ArrayList<Double>();
	private static List<Double> rewardsForQLearning = new ArrayList<Double>();
	
	public static void addNumberOfIterations(Integer numIterations1){
		numIterations.add(numIterations1);
	}
	public static void addStepsToFinishValueIteration(Integer stepsToFinishValueIteration1){
		stepsToFinishValueIteration.add(stepsToFinishValueIteration1);
	}
	public static void addStepsToFinishPolicyIteration(Integer stepsToFinishPolicyIteration1){
		stepsToFinishPolicyIteration.add(stepsToFinishPolicyIteration1);
	}
	public static void addStepsToFinishQLearning(Integer stepsToFinishQLearning1){
		stepsToFinishQLearning.add(stepsToFinishQLearning1);
	}
	public static void printValueIterationResults(PrintWriter pw, String difficulty){
		System.out.print("Value Iteration,");	
		printList(stepsToFinishValueIteration, "Value Iteration", "Results", pw, difficulty);
	}
	public static void printPolicyIterationResults(PrintWriter pw, String difficulty){
		System.out.print("Policy Iteration,");	
		printList(stepsToFinishPolicyIteration, "Policy Iteration", "Results", pw, difficulty);
	}
	public static void printQLearningResults(PrintWriter pw, String difficulty){
		System.out.print("Q Learning,");	
		printList(stepsToFinishQLearning, "Q Learning", "Results", pw, difficulty);
	}
	

	public static void addMillisecondsToFinishValueIteration(Integer millisecondsToFinishValueIteration1){
		millisecondsToFinishValueIteration.add(millisecondsToFinishValueIteration1);
	}
	public static void addMillisecondsToFinishPolicyIteration(Integer millisecondsToFinishPolicyIteration1){
		millisecondsToFinishPolicyIteration.add(millisecondsToFinishPolicyIteration1);
	}
	public static void addMillisecondsToFinishQLearning(Integer millisecondsToFinishQLearning1){
		millisecondsToFinishQLearning.add(millisecondsToFinishQLearning1);
	}
	public static void addValueIterationReward(double reward) {
		rewardsForValueIteration.add(reward);
	}
	public static void addPolicyIterationReward(double reward) {
		rewardsForPolicyIteration.add(reward);
	}
	public static void addQLearningReward(double reward) {
		rewardsForQLearning.add(reward);
	}
	public static void printValueIterationTimeResults(PrintWriter pw, String difficulty){
		System.out.print("Value Iteration,");	
		printList(millisecondsToFinishValueIteration, "Value Iteration", "Time Results", pw, difficulty);
	}
	public static void printPolicyIterationTimeResults(PrintWriter pw, String difficulty){
		System.out.print("Policy Iteration,");
		printList(millisecondsToFinishPolicyIteration, "Policy Iteration", "Time Results", pw, difficulty);
	}

	public static void printQLearningTimeResults(PrintWriter pw, String difficulty){
		System.out.print("Q Learning,");	
		printList(millisecondsToFinishQLearning, "Q Learning", "Time Results", pw, difficulty);
	}

	public static void printValueIterationRewards(PrintWriter pw, String difficulty){
		System.out.print("Value Iteration Rewards,");
		printDoubleList(rewardsForValueIteration, "Value Iteration", "Rewards", pw, difficulty);
	}

	public static void printPolicyIterationRewards(PrintWriter pw, String difficulty){
		System.out.print("Policy Iteration Rewards,");
		printDoubleList(rewardsForPolicyIteration, "Policy Iteration", "Rewards", pw, difficulty);
	}

	public static void printQLearningRewards(PrintWriter pw, String difficulty){
		System.out.print("Q Learning Rewards,");
		printDoubleList(rewardsForQLearning, "Q Learning", "Rewards", pw, difficulty);
	}

	public static void printNumIterations(PrintWriter pw, String difficulty){
		System.out.print("Iterations,");	
		printList(numIterations, "none", "Iterations", pw, difficulty);
	}
	private static void printList(List<Integer> valueList, String learner, String measure, PrintWriter pw, String difficulty){
		int counter = 0;
		for(int value : valueList) {
			System.out.print(String.valueOf(value));
			if(learner!="none"){
				StringBuilder sb = new StringBuilder();
				sb.append(counter+1);
				sb.append(", ");
				sb.append(difficulty);
				sb.append(", ");
			sb.append(learner);
			sb.append(", ");
			sb.append(measure);
			sb.append(", ");
			sb.append(String.valueOf(value));
			sb.append("\n");
			pw.write(sb.toString());
		}
			if(counter != valueList.size()-1){
				System.out.print(",");
			}
			counter++;
		}
		System.out.println();
	}
	private static void printDoubleList(List<Double> valueList, String learner, String measure, PrintWriter pw, String difficulty){
		int counter = 0;
		for(double value : valueList){
			System.out.print(String.valueOf(value));
			StringBuilder sb = new StringBuilder();
			sb.append(counter+1);
			sb.append(", ");
			sb.append(difficulty);
			sb.append(", ");
			sb.append(learner);
			sb.append(", ");
			sb.append(measure);
			sb.append(", ");
			sb.append(String.valueOf(value));
			sb.append("\n");
			pw.write(sb.toString());
			if(counter != valueList.size()-1){
				System.out.print(",");
			}
			counter++;
		}
		System.out.println();
	}
	public static void printAggregateAnalysis(PrintWriter pw, String difficulty){
		System.out.println("//Aggregate Analysis//\n");
		System.out.println("The data below shows the number of steps/actions the agent required to reach \n"
				+ "the terminal state given the number of iterations the algorithm was run.");
		printNumIterations(pw, difficulty);
		printValueIterationResults(pw, difficulty);
		printPolicyIterationResults(pw, difficulty);
		printQLearningResults(pw, difficulty);
		System.out.println();
		System.out.println("The data below shows the number of milliseconds the algorithm required to generate \n"
				+ "the optimal policy given the number of iterations the algorithm was run.");
		printNumIterations(pw, difficulty);
		printValueIterationTimeResults(pw, difficulty);
		printPolicyIterationTimeResults(pw, difficulty);
		printQLearningTimeResults(pw, difficulty);

		System.out.println("\nThe data below shows the total reward gained for \n"
				+ "the optimal policy given the number of iterations the algorithm was run.");
		printNumIterations(pw, difficulty);
		printValueIterationRewards(pw, difficulty);
		printPolicyIterationRewards(pw, difficulty);
		printQLearningRewards(pw, difficulty);
	}
}
