package burlap.behavior.singleagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burlap.behavior.singleagent.options.Option;
import burlap.behavior.singleagent.options.support.OptionEvaluatingRF;
import burlap.behavior.statehashing.StateHashFactory;
import burlap.behavior.statehashing.StateHashTuple;
import burlap.debugtools.DPrint;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

/**
 * The super class to use for various MDP solving algorithms, including both planning and learning algorithms. It provides
 * the common data members that most all algorithms will need to use
 * and provides methods for manipulating them that are common.
 * @author James MacGlashan
 *
 */
public abstract class MDPSolver {

	/**
	 * The domain to solve
	 */
	protected Domain												domain;
	
	/**
	 * The hashing factory to use for hashing states in tabular solvers
	 */
	protected StateHashFactory										hashingFactory;
	
	/**
	 * The task reward function
	 */
	protected RewardFunction										rf;
	
	/**
	 * The terminal function for identifying terminal states
	 */
	protected TerminalFunction										tf;
	
	/**
	 * The MDP discount factor
	 */
	protected double												gamma;
	
	
	/**
	 * The list of actions this solver can use. May include non-domain specified actions like {@link burlap.behavior.singleagent.options.Option}s.
	 */
	protected List <Action>											actions;
	
	/**
	 * A mapping to internal stored hashed states ({@link burlap.behavior.statehashing.StateHashTuple}) that are stored.
	 * Useful since two identical states may have different object instance name identifiers
	 * that can affect the parameters in GroundedActions.
	 */
	protected Map <StateHashTuple, StateHashTuple>					mapToStateIndex;
	
	/**
	 * Indicates whether the action set for this planner includes parametrized actions
	 */
	protected boolean												containsParameterizedActions;
	
	
	/**
	 * The debug code use for calls to {@link burlap.debugtools.DPrint}
	 */
	protected int													debugCode;
	
	
	/**
	 * This method will cause the planner to begin planning from the specified initial state
	 * @param initialState the initial state of the planning problem
	 */
	public abstract void planFromState(State initialState);
	
	
	/**
	 * This method resets all solver results so that a solver can be restarted fresh
	 * as if had never solved the MDP.
	 */
	public abstract void resetSolver();
	
	/**
	 * Initializes the solver with the common elements.
	 * @param domain the domain to be solved.
	 * @param rf the reward function
	 * @param tf the terminal state function
	 * @param gamma the MDP discount factor
	 * @param hashingFactory the hashing factory used to store states (may be set to null if the solver is not tabular)
	 */
	public void solverInit(Domain domain, RewardFunction rf, TerminalFunction tf, double gamma, StateHashFactory hashingFactory){
		
		this.domain = domain;
		this.rf = rf;
		this.tf = tf;
		this.gamma = gamma;
		this.hashingFactory = hashingFactory;
		
		mapToStateIndex = new HashMap<StateHashTuple, StateHashTuple>();
		
		containsParameterizedActions = false;
		List <Action> actions = domain.getActions();
		this.actions = new ArrayList<Action>(actions.size());
		for(Action a : actions){
			this.actions.add(a);
			if(a instanceof Option){
				Option o = (Option)a;
				o.keepTrackOfRewardWith(rf, gamma);
				o.setExernalTermination(tf);
				o.setExpectationHashingFactory(hashingFactory);
				if(!(this.rf instanceof OptionEvaluatingRF)){
					this.rf = new OptionEvaluatingRF(this.rf);
				}
			}
			if(a.getParameterClasses().length > 0){
				containsParameterizedActions = true;
			}
		}
		
	}
	
	
	/**
	 * Adds an additional action the solver that is not included in the domain definition. For instance, an {@link burlap.behavior.singleagent.options.Option}
	 * should be added using this method.
	 * @param a the action to add to the solver
	 */
	public void addNonDomainReferencedAction(Action a){
		//make sure it doesn't already exist in the list
		if(!actions.contains(a)){
			actions.add(a);
			if(a instanceof Option){
				Option o = (Option)a;
				o.keepTrackOfRewardWith(rf, gamma);
				o.setExernalTermination(tf);
				if(!(this.rf instanceof OptionEvaluatingRF)){
					this.rf = new OptionEvaluatingRF(this.rf);
				}
			}
			if(a.getParameterClasses().length > 0){
				this.containsParameterizedActions = true;
			}
		}
		
	}
	
	
	/**
	 * Sets the action set the planner should use.
	 * @param actions the actions the planner should use.
	 */
	public void setActions(List<Action> actions){
		this.actions = actions;
	}


	/**
	 * Returns a copy of all actions this planner uses for reasoning; including added actions that are not part of the
	 * domain specification (e.g., {@link burlap.behavior.singleagent.options.Option}s). Modifying
	 * the returned list will not modify the action list this planner uses.
	 * @return a {@link java.util.List} of all actions this planner uses.
	 */
	public List<Action> getActions(){
		return new ArrayList<Action>(this.actions);
	}
	
	/**
	 * Returns the {@link burlap.oomdp.core.TerminalFunction} this planner uses.
	 * @return the {@link burlap.oomdp.core.TerminalFunction} this planner uses.
	 */
	public TerminalFunction getTF(){
		return tf;
	}
	
	
	/**
	 * Returns the {@link burlap.oomdp.singleagent.RewardFunction} this planner uses.
	 * @return the {@link burlap.oomdp.singleagent.RewardFunction} this planner uses.
	 */
	public RewardFunction getRF(){
		return rf;
	}
	
	
	/**
	 * Returns the {@link burlap.behavior.statehashing.StateHashFactory} this planner uses.
	 * @return the {@link burlap.behavior.statehashing.StateHashFactory} this planner uses.
	 */
	public StateHashFactory getHashingFactory(){
		return this.hashingFactory;
	}
	
	
	
	/**
	 * Sets the reward function used by this planner
	 * @param rf the reward function to be used by this planner
	 */
	public void setRf(RewardFunction rf) {
		this.rf = rf;
	}

	/**
	 * Sets the terminal state function used by this planner
	 * @param tf the terminal function to be used by this planner
	 */
	public void setTf(TerminalFunction tf) {
		this.tf = tf;
	}

	/**
	 * Returns gamma, the discount factor used by this planner
	 * @return gamma, the discount factor used by this planner
	 */
	public double getGamma(){
		return this.gamma;
	}
	
	/**
	 * Sets gamma, the discount factor used by this planner
	 * @param gamma the discount factor used by this planner
	 */
	public void setGamma(double gamma){
		this.gamma = gamma;
	}

	/**
	 * Sets the debug code to be used by calls to {@link burlap.debugtools.DPrint}
	 * @param code the code to be used by {@link burlap.debugtools.DPrint}
	 */
	public void setDebugCode(int code){
		this.debugCode = code;
	}
	
	
	/**
	 * Returns the debug code used by this planner for calls to {@link burlap.debugtools.DPrint}
	 * @return the debug code used by this planner for calls to {@link burlap.debugtools.DPrint}
	 */
	public int getDebugCode(){
		return debugCode;
	}
	
	
	/**
	 * Toggles whether the planner's calls to {@link burlap.debugtools.DPrint} should be printed.
	 * @param toggle whether to print the calls to {@link burlap.debugtools.DPrint}
	 */
	public void toggleDebugPrinting(boolean toggle){
		DPrint.toggleCode(debugCode, toggle);
	}


	/**
	 * Sets the domain of this planner. NOTE: this will also reset the actions this planner uses to the actions of the
	 * provided domain. If you have previously added non-domain referenced actiosn through the {@link #addNonDomainReferencedAction(burlap.oomdp.singleagent.Action)}
	 * method, you will have to do so again.
	 * @param domain the domain this planner should use.
	 */
	public void setDomain(Domain domain) {
		this.domain = domain;
		this.actions.clear();
		for(Action a : domain.getActions()){
			this.actions.add(a);
		}
	}

	public Domain getDomain() {
		return domain;
	}

	public RewardFunction getRf() {
		return rf;
	}

	public TerminalFunction getTf() {
		return tf;
	}

	/**
	 * Takes a source parametrized GroundedAction and a matching between object instances of two different states and returns a GroudnedAction
	 * with parameters using the matched parameters. This method is useful a stored state and action pair in the planner data structure has different
	 * object name identifiers than a query state that is otherwise identical. The matching is from the state in which the source action is applied
	 * to some target state that is not provided to this method.
	 * @param a the source action that needs to be translated
	 * @param matching a map from object instance names to other object instance names.
	 * @return and new GroundedAction with object parametrization that follow from the matching
	 */
	protected GroundedAction translateAction(GroundedAction a, Map <String,String> matching){
		String [] newParams = new String[a.params.length];
		for(int i = 0; i < a.params.length; i++){
			newParams[i] = matching.get(a.params[i]);
		}
		return new GroundedAction(a.action, newParams);
	}
	
	/**
	 * A shorthand method for hashing a state.
	 * @param s the state to hash
	 * @return a StateHashTuple produce from this planners StateHashFactory.
	 */
	public StateHashTuple stateHash(State s){
		return hashingFactory.hashState(s);
	}
	
	
	/**
	 * Returns all grounded actions in the provided state for all the actions that this planner can use.
	 * @param s the source state for which to get all GroundedActions.
	 * @return all GroundedActions.
	 */
	protected List <GroundedAction> getAllGroundedActions(State s){

		return Action.getAllApplicableGroundedActionsFromActionList(this.actions, s);
		
	}
	
}