package vnsBFP;

import java.util.ArrayList;

public class Job
{
	// Elementi caratterizzanti il job
	private long releaseTime;
	private long executionTime;
	private long dueDate;
	private int weight;
	private String name;
	
	// Elementi che rendono semplice il calcolo delle precedenze
	private long startingTime;
	private long endingTime;
	private ArrayList<String> previousJob = null;
	private ArrayList<String> nextJob = null;
	private ArrayList<Integer> jobPrecedenti = null;
	private ArrayList<Integer> jobSuccessivi = null;
	private int machine;
	private int numberOnMachine;
	
	// Elementi necessari per la soluzione iniziale
	private double priorityIndex;
	private int setupTimes[];
	private float setupMedio;
	private int indexOfJob;

	
	public Job(String jobName, long relTime, long execTime, long dueDateTime, int weightCost, int numeroJob, int index)
	{
		releaseTime = relTime;
		executionTime = execTime;
		dueDate = dueDateTime;
		weight = weightCost;
		name = jobName;
		priorityIndex = 0;
		setupTimes = new int[numeroJob];
		setupMedio = 0;
		indexOfJob = index;
		endingTime = 0;

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
	
	public String getName()
	{
		return name;
	}
	
	public ArrayList<String> getPreviousJob()
	{
		return previousJob;
	}

	public ArrayList<String> getNextJob()
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
		return setupTimes[i];
	}
	
	public float getSetupMedio()
	{
		return setupMedio;
	}
	
	public int getIndexOfJob()
	{
		return indexOfJob;
	}
	
	public ArrayList<Integer> getjobPrecedenti()
	{
		return jobPrecedenti;
	}
	
	public ArrayList<Integer> getjobSuccessivi()
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
	
	public void setPreviousJob(String prev)
	{
		if(previousJob == null)
		{
			previousJob = new ArrayList<String>();
			previousJob.add(prev);
		}
			
		else
			previousJob.add(prev);
	}
	
	public void setNextJob(String next)
	{
		if(nextJob == null)
		{
			nextJob = new ArrayList<String>();
			nextJob.add(next);
		}
			
		else
			nextJob.add(next);
	}
	
	public void setJobPrecedenti(int next)
	{
		if(jobPrecedenti == null)
		{
			jobPrecedenti = new ArrayList<Integer>();
			jobPrecedenti.add(next);
		}
			
		else
			jobPrecedenti.add(next);
	}
	
	public void setJobSuccessivi(int next)
	{
		if(jobSuccessivi == null)
		{
			jobSuccessivi = new ArrayList<Integer>();
			jobSuccessivi.add(next);
		}
			
		else
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
	
	public void setIndexOfJob(int i)
	{
		indexOfJob = i;
	}
	
	public String toString() {
		/*return name + " (r:" + this.releaseTime + ", e:"
				+ this.executionTime + ", dd:" + this.dueDate + ", w:" + this.weight;*/
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dueDate ^ (dueDate >>> 32));
		result = prime * result + Float.floatToIntBits(endingTime);
		result = prime * result
				+ (int) (executionTime ^ (executionTime >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nextJob == null) ? 0 : nextJob.hashCode());
		result = prime * result
				+ ((previousJob == null) ? 0 : previousJob.hashCode());
		result = prime * result + (int) (releaseTime ^ (releaseTime >>> 32));
		result = prime * result + Float.floatToIntBits(startingTime);
		result = prime * result + weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Job other = (Job) obj;
		if (dueDate != other.dueDate)
			return false;
		if (Float.floatToIntBits(endingTime) != Float
				.floatToIntBits(other.endingTime))
			return false;
		if (executionTime != other.executionTime)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nextJob == null) {
			if (other.nextJob != null)
				return false;
		} else if (!nextJob.equals(other.nextJob))
			return false;
		if (previousJob == null) {
			if (other.previousJob != null)
				return false;
		} else if (!previousJob.equals(other.previousJob))
			return false;
		if (releaseTime != other.releaseTime)
			return false;
		if (Float.floatToIntBits(startingTime) != Float
				.floatToIntBits(other.startingTime))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}

	public boolean hasPredecessor() {
		// TODO Auto-generated method stub
		return false;
	}

	public ArrayList<Job> getPredecessors() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
