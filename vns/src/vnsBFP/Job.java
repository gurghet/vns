package vnsBFP;

public class Job {
	public int id;
	public int procTime;
	public int dueDate;
	public int weight;
	/*
	 * Bisogna valutare se e come inserire qui i set-up times, ad
	 * esempio in un array e i vincoli di precedenza
	 * */
	
	@Override
	public String toString() {
		return "[id:"+ this.id +" procTime:"+ this.procTime
				+" dueDate:"+ this.dueDate +" weight:"+ this.weight +"]";
	}
}
