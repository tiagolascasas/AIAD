/**
 * 
 */
package smartSemaphores;

import java.util.ArrayList;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.ui.RunOptionsModel;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;
import smartSemaphores.jade.SemaphoricAgent;
import smartSemaphores.jade.SinkAgent;
import smartSemaphores.repast.SimulationManager;

/**
 * Launcher for Repast
 */
public class SmartSemaphoresRepastLauncher extends RepastSLauncher
{
	private static float ticksPerSecond = 1.0f;
	private static int hours = 5;
	
	private ContainerController mainContainer;
	private ContainerController crossContainerA;
	private ContainerController crossContainerB;
	private ContainerController crossContainerC;
	private ArrayList<Agent> agents;
	
	private SimulationManager manager;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see sajas.sim.repasts.RepastSLauncher#getName()
	 */
	@Override
	public String getName()
	{
		return "SmartSemaphores";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sajas.sim.repasts.RepastSLauncher#launchJADE()
	 */
	@SuppressWarnings("unused")
	@Override
	protected void launchJADE()
	{
		Runtime rt = Runtime.instance();
		Profile p0 = new ProfileImpl();
		Profile p1 = new ProfileImpl();
		Profile p2 = new ProfileImpl();
		Profile p3 = new ProfileImpl();

		this.mainContainer = rt.createMainContainer(p0);
		this.crossContainerA = rt.createAgentContainer(p1);
		this.crossContainerB = rt.createAgentContainer(p2);
		this.crossContainerC = rt.createAgentContainer(p3);
		
		this.agents = new ArrayList<>();
		
		launchAgents();
		
		int[] sources = {1, 8, 10, 6, 15, 17};
		int[] middles = {3, 4, 11, 12, 13, 14};
		int[] sinks = {2, 7, 9, 5, 16, 18};
		this.manager.init(sources, middles, sinks);
	}

	private void launchAgents()
	{		
		try
		{
			// Semaphoric agents on road cross A
			int[] crossAagents = { 1, 11, 4, 8 };
			for (int i : crossAagents)
			{
				SemaphoricAgent agent = new SemaphoricAgent(i, crossAagents,10);
				this.crossContainerA.acceptNewAgent("Semaphoric agent " + i, agent).start();
				this.agents.add(agent);
			}

			// Semaphoric agents on road cross B
			int[] crossBagents = { 3, 13, 6, 10 };
			for (int i : crossBagents)
			{
				SemaphoricAgent agent = new SemaphoricAgent(i, crossBagents,10);
				this.crossContainerB.acceptNewAgent("Semaphoric agent " + i, agent).start();
				this.agents.add(agent);
			}

			// Semaphoric agents on road cross C
			int[] crossCagents = { 14, 17, 15, 12 };
			for (int i : crossCagents)
			{
				SemaphoricAgent agent = new SemaphoricAgent(i, crossCagents,10);
				this.crossContainerC.acceptNewAgent("Semaphoric agent " + i, agent).start();
				this.agents.add(agent);
			}

			// Sink agents (two sinks per cross)
			SinkAgent sinkAgent;
			
			sinkAgent = new SinkAgent(2);
			this.crossContainerA.acceptNewAgent("Sink agent 2", sinkAgent).start();
			this.agents.add(sinkAgent);
			
			sinkAgent = new SinkAgent(7);
			this.crossContainerA.acceptNewAgent("Sink agent 7", sinkAgent).start();
			this.agents.add(sinkAgent);
			
			sinkAgent = new SinkAgent(5);
			this.crossContainerB.acceptNewAgent("Sink agent 5", sinkAgent).start();
			this.agents.add(sinkAgent);
			
			sinkAgent = new SinkAgent(9);
			this.crossContainerB.acceptNewAgent("Sink agent 9", sinkAgent).start();
			this.agents.add(sinkAgent);
			
			sinkAgent = new SinkAgent(16);
			this.crossContainerC.acceptNewAgent("Sink agent 16", sinkAgent).start();
			this.agents.add(sinkAgent);
			
			sinkAgent = new SinkAgent(18);
			this.crossContainerC.acceptNewAgent("Sink agent 18", sinkAgent).start();
			this.agents.add(sinkAgent);
		} catch (StaleProxyException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Context<?> build(Context<Object> context)
	{
		// http://repast.sourceforge.net/docs/RepastJavaGettingStarted.pdf
		
		initSimulation();

		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("SmartSemaphores Road Network", context, true);
		netBuilder.buildNetwork();
		
		this.manager = new SimulationManager(this);
		context.add(this.manager);

		return super.build(context);
	}

	private void initSimulation()
	{
		int ticks = (int)(this.ticksPerSecond * 3600 * this.hours);
		RunEnvironment.getInstance().endAt(ticks);
		RunEnvironment.getInstance().setScheduleTickDelay(70);
	}

	public static Agent getAgent(Context<?> context, String name)
	{
		for (Object obj : context.getObjects(Agent.class))
		{
			if (((Agent) obj).getAID().getName().equals(name))
			{
				return (Agent) obj;
			}
		}
		return null;
	}

	public Agent getAgent(int i)
	{
		String id = SemaphoricAgent.makeFullName(i);
		
		for (Agent agent : this.agents)
		{
			if (agent.getAID().getName().equals(id))
				return agent;
		}
		return null;
	}
}
