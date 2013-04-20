package vnsBFP;

public class Job
{
	private long releaseTime;
	private long executionTime;
	private long dueDate;
	private int weight;
	private String name;
	
	/**
	 * @param jobName String identificativo del job
	 * @param relTime release time
	 * @param execTime execution time
	 * @param dueDateTime due date
	 * @param weightCost peso del job nel TWT
	 */
	public Job(String jobName, long relTime, long execTime, long dueDateTime, int weightCost)
	{
		releaseTime = relTime;
		executionTime = execTime;
		dueDate = dueDateTime;
		weight = weightCost;
		name = jobName;
	}
	
	public long getRelaseTime()
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
	
	public void setDueDate(long time)
	{
		dueDate = time;
	}
	
	public void setExecutionTime(long time)
	{
		dueDate = time;
	}

	public void setRelaseTime(long time)
	{
		dueDate = time;
	}
	
	public void setWeight(int w)
	{
		weight = w;
	}
	
	public String toString() {
		/*return name + " (r:" + this.releaseTime + ", e:"
				+ this.executionTime + ", dd:" + this.dueDate + ", w:" + this.weight;*/
		return name;
		
	}
}
