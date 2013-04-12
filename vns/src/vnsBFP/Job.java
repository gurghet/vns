package vnsBFP;

public class Job
{
	private float releaseTime;
	private float executionTime;
	private float dueDate;
	private float weight;
	private String name;
	
	/**
	 * @param jobName String identificativo del job
	 * @param relTime release time
	 * @param execTime execution time
	 * @param dueDateTime due date
	 * @param weightCost peso del job nel TWT
	 */
	public Job(String jobName, float relTime, float execTime, float dueDateTime, float weightCost)
	{
		releaseTime = relTime;
		executionTime = execTime;
		dueDate = dueDateTime;
		weight = weightCost;
		name = jobName;
	}
	
	public float getRelaseTime()
	{
		return releaseTime;
	}
	
	public float getExecutionTime()
	{
		return executionTime;
	}
	
	public float getDueDate()
	{
		return dueDate;
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setDueDate(float time)
	{
		dueDate = time;
	}
	
	public void setExecutionTime(float time)
	{
		dueDate = time;
	}

	public void setRelaseTime(float time)
	{
		dueDate = time;
	}
	
	public void setWeight(float w)
	{
		weight = w;
	}
	
	public String toString() {
		return name + " (r:" + this.releaseTime + ", e:"
				+ this.executionTime + ", dd:" + this.dueDate + ", w:" + this.weight;
		
	}
}
