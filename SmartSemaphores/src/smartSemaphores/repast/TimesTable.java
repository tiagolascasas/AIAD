/*
 * SmartSemaphores
 * @author      Nadia Carvalho
 * @author      Ruben Torres
 * @author	Tiago Santos
 * @version       0.1.0
 */
package smartSemaphores.repast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import smartSemaphores.SmartSemaphores;

/**
 * The Class TimesTable.
 */
public class TimesTable {

    /** The vehicles. */
    private ArrayList<Vehicle> vehicles;

    /** The table. */
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> table;

    /**
     * Instantiates a new times table.
     *
     * @param vehicles
     *            the vehicles
     */
    public TimesTable(ArrayList<Vehicle> vehicles) {
	this.vehicles = vehicles;
	this.table = new HashMap<>();

	Set<Integer> sources = new TreeSet<>();
	for (Vehicle v : this.vehicles) {
	    sources.add(v.getOriginPoint());
	}
	Set<Integer> sinks = new TreeSet<>();
	for (Vehicle v : this.vehicles) {
	    sinks.add(v.getEndPoint());
	}

	for (Integer i : sources) {
	    HashMap<Integer, ArrayList<Integer>> sinksMap = new HashMap<>();
	    for (Integer j : sinks)
		sinksMap.put(j, new ArrayList<Integer>());
	    this.table.put(i, sinksMap);
	}

	for (Vehicle v : this.vehicles) {
	    int source = v.getOriginPoint();
	    int sink = v.getEndPoint();

	    ArrayList<Integer> times = this.table.get(source).get(sink);
	    times.add(v.getElapsedTime());
	    this.table.get(source).put(sink, times);
	}
    }

    /**
     * Prints the table to stdout.
     */
    public void printTableToStdout() {
	for (Integer source : this.table.keySet()) {
	    for (Integer sink : this.table.get(source).keySet()) {
		int average = getAverage(this.table.get(source).get(sink));
		System.out.println(source + "->" + sink + ": " + average + "s");
	    }
	}
    }
    
    public String getCSV()
    {
    	String s = "";
    	for (Integer source : this.table.keySet()) {
    	    for (Integer sink : this.table.get(source).keySet()) 
    	    {
	    		int average = getAverage(this.table.get(source).get(sink));
	    		StringBuilder b = new StringBuilder();
	    		b.append(average).append(",")
	    		.append(source).append(",")
	    		.append(sink).append(",")
	    		.append(SmartSemaphores.SIMULATION_TYPE).append(",")
	    		.append(SmartSemaphores.EMERGENCY_PROBABILITY).append(",")
	    		.append(SmartSemaphores.PEDESTRIAN_PROBABILITY).append(",")
	    		.append(SmartSemaphores.EXIT_RATE).append("\n");
	    		s += b.toString();
    	    }
    	}
    	return s;
    }

    /**
     * Gets the average.
     *
     * @param times
     *            the times
     * @return the average
     */
    private int getAverage(ArrayList<Integer> times) {
	int sum = 0;
	for (Integer i : times)
	    sum += i;
	return (int) ((float) sum / (float) times.size());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuilder string = new StringBuilder();
	for (Integer source : this.table.keySet()) {
	    for (Integer sink : this.table.get(source).keySet()) {
		int average = getAverage(this.table.get(source).get(sink));
		string.append(source + "," + sink + "," + average + "\n");
	    }
	}
	return string.toString();
    }
}
