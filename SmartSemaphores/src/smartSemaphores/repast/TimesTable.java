package smartSemaphores.repast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class TimesTable
{
	private ArrayList<Vehicle> vehicles;
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> table;

	public TimesTable(ArrayList<Vehicle> vehicles)
	{
		this.vehicles = vehicles;
		this.table = new HashMap<>();
		
		Set<Integer> sources = new TreeSet<>();
		for (Vehicle v : this.vehicles)
		{
			sources.add(v.getOriginPoint());
		}
		Set<Integer> sinks = new TreeSet<>();
		for (Vehicle v : this.vehicles)
		{
			sinks.add(v.getEndPoint());
		}
		
		for (Integer i : sources)
		{
			HashMap<Integer, ArrayList<Integer>> sinksMap = new HashMap<>();
			for (Integer j : sinks)
				sinksMap.put(j, new ArrayList<Integer>());
			this.table.put(i, sinksMap);
		}
		
		for (Vehicle v : this.vehicles)
		{
			int source = v.getOriginPoint();
			int sink = v.getEndPoint();
			
			ArrayList<Integer> times = this.table.get(source).get(sink);
			times.add(v.getElapsedTime());
			this.table.get(source).put(sink, times);
		}
	}
	
	public void printTableToStdout()
	{
		for (Integer source : this.table.keySet())
		{
			for (Integer sink : this.table.get(source).keySet())
			{
				int average = getAverage(this.table.get(source).get(sink));
				System.out.println(source + "->" + sink + ": " + average + "s");
			}
		}
	}

	private int getAverage(ArrayList<Integer> times)
	{
		int sum = 0;
		for (Integer i : times)
			sum += i;
		return (int)((float)sum / (float)times.size());
	}
}
