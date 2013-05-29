package vnsBFP;

import java.util.ArrayList;
import java.util.HashMap;

public class Job
{
	private static final HashMap<Integer, Job> jobMap = new HashMap<Integer, Job>();
	
	// Elementi caratterizzanti il job
	private long releaseTime;
	private long executionTime;
	private long dueDate;
	private int weight;
	
	// Elementi che rendono semplice il calcolo delle precedenze
	private long startingTime;
	private long endingTime;
	private ArrayList<Job> previousJob = new ArrayList<Job>();
	private ArrayList<Job> nextJob = new ArrayList<Job>();
	private ArrayList<Job> jobPrecedenti = new ArrayList<Job>();
	private ArrayList<Job> jobSuccessivi = new ArrayList<Job>();
	private int machine;
	private int numberOnMachine;
	
	// Elementi necessari per la soluzione iniziale
	private double priorityIndex;
	private int setupTimes[];
	private float setupMedio;
	private int jobID;

	
	public Job(long relTime, long execTime, long dueDateTime, int weightCost, int numeroJob, int index)
	{
		releaseTime = relTime;
		executionTime = execTime;
		dueDate = dueDateTime;
		weight = weightCost;
		priorityIndex = 0;
		setupTimes = new int[numeroJob];
		for(int i = 0 ; i<numeroJob; i++)
		{
			setupTimes[i]=0;
		}
		setupMedio = 0;
		jobID = index;
		endingTime = 0;

		jobMap.put(index, this);
	}
	
	public static Job getJobByID(int id) {
		return jobMap.get(id);
	}
	
	public long getReleaseTime()
	{
		return releaseTime;
	}
	
	public long getExecutionTime()
	{
		return executionTime;
	}
	
	public long getDueDate()
	{
		return dueDate;
	}
	
	public int getWeight()
	{
		return weight;
	}
	
	public ArrayList<Job> getPreviousJob()
	{
		return previousJob;
	}

	public ArrayList<Job> getNextJob()
	{
		return nextJob;
	}
	
	public long getStartingTime()
	{
		return startingTime;
	}
	
	public long getEndingTime()
	{
		return endingTime;
	}
	
	public double getPriorityIndex()
	{
		return priorityIndex;
	}
	
	public int getSetupTimes(int i)
	{
		if (i == -1) return 0;
		return setupTimes[i];
	}
	
	public float getSetupMedio()
	{
		return setupMedio;
	}
	
	public int getIndexOfJob()
	{
		return jobID;
	}
	
	public ArrayList<Job> getPredecessors()
	{
		return jobPrecedenti;
	}
	
	public ArrayList<Job> getSuccessors()
	{
		return jobSuccessivi;
	}
	
	public int getMachine()
	{
		return machine;
	}
	
	public int getNumberOnMachine()
	{
		return numberOnMachine;
	}
	
	
	public void setDueDate(long time)
	{
		dueDate = time;
	}
	
	public void setExecutionTime(long time)
	{
		executionTime = time;
	}

	public void setRelaseTime(long time)
	{
		releaseTime = time;
	}
	
	public void setWeight(int w)
	{
		weight = w;
	}
	
	public void addPreviousJob(Job prev)
	{
		previousJob.add(prev);
	}
	
	public void addNextJob(Job next)
	{
		nextJob.add(next);
	}
	
	public void addPredecessor(Job prec)
	{
		jobPrecedenti.add(prec);
	}
	
	public void addSuccessor(Job next)
	{
		jobSuccessivi.add(next);
	}
	
	public void setMachine(int m)
	{
		machine = m;
	}
	
	public void setNumberOnMachine(int n)
	{
		numberOnMachine = n;
	}
	
	public void setEndingTime(long endTime)
	{
		endingTime = endTime;
	}
	
	public void setStartingTime(long startTime)
	{
		startingTime = startTime;
	}	
	
	public void setPriorityIndex(double f)
	{
		priorityIndex = f;
	}
	
	public void setSetupTimes(int index, int value)
	{
		setupTimes[index] = value;
	}
	
	public void setSetupMedio(float s)
	{
		setupMedio = s;
	}
	
	public boolean hasPredecessor() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toString() {
		return String.valueOf(jobID);
	}
}