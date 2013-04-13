package vnsBFP;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;


public class StorageVNS
{
	// ArrayList che memorizza tutti i job divisi per macchine.
	private ArrayList<ArrayList<Job>> allMachines = null;
	
	// Array delle posizioni correnti.
	private int[] currentPositionArray = null;
	
	// TWT attuale.
	private float twt;
	
	
	
	public StorageVNS(int nMachines)
	{
		currentPositionArray = new int[nMachines];
		allMachines = new ArrayList<ArrayList<Job>>();
		
		for(int x = 0; x < nMachines ; x++)
		{
			allMachines.add(new ArrayList<Job>());
			currentPositionArray[x] = 0;
		}
	}
	
	/**
	 * Costruttore di copia
	 * @param storageVNS StorageVNS da clonare
	 */
	public StorageVNS(StorageVNS storageVNS) {
		this.allMachines = new ArrayList<ArrayList<Job>>();
		for(int x = 0; x < storageVNS.getNumberOfMachines() ; x++)
		{
			allMachines.add(new ArrayList<Job>(storageVNS.getMachine(x)));
		}
		this.currentPositionArray = storageVNS.getCurrentPositionArray().clone();
		this.twt = storageVNS.getTwt();
	}
	
	protected ArrayList<ArrayList<Job>> getAllMachines() {
		return allMachines;
	}

	protected int[] getCurrentPositionArray() {
		return currentPositionArray;
	}

	protected float getTwt() {
		return twt;
	}

	public int getNumberOfMachines()
	{
		return allMachines.size();
	}
	
	public int getNumberOfJobsOnMachine(int machineNumber)
	{
		return allMachines.get(machineNumber).size();
	}
	
	public ArrayList<Job> getMachine(int position)
	{
		return allMachines.get(position);
	}
	
	// Metodi di inserimento e cancellazione
	/**
	 * @param machineNumber numero della macchina
	 * @param j Job da aggiungere
	 */
	public void addJobOnMachine(int machineNumber, Job j)
	{
		allMachines.get(machineNumber).add(j);
	}
	
	public void removeJobFromMachine(int position, int machineNumber)
	{
		allMachines.get(machineNumber).remove(position);
	}
	
	public void removeAllJobsFromMachine(int machineNumber)
	{
		for(int x = 0; x < allMachines.get(machineNumber).size(); x++)
		{
			allMachines.get(machineNumber).remove(x);
		}
	}
	
	// Mosse
	
	/**
	 * @param range raggio in cui verranno effettuati gli swap
	 * @param repeat quante volte verrà ripetuta questa mossa
	 * alla fine sto metodo fa un po’ le cose a casaccio, ma in
	 * fondo è un algoritmo random no?
	 */
	public void swapOnOneMachine(int range, int repeat) {
		for (int i = 0; i < this.getNumberOfMachines(); i++) {
			// range = 0 è come dire range = nmax
			if (range == 0) range = this.getNumberOfJobsOnMachine(i);
			for (int j = 0; j < this.getNumberOfJobsOnMachine(i); j++) {
				_swapOnOneMachine_onMachine_onPosition(range, repeat, allMachines.get(i), j);
			}
		}
	}
	
	private void _swapOnOneMachine_onMachine_onPosition(int range, int repeat, ArrayList<Job> machine, int position)
	{
		Job consideredJob = machine.get(position);
		int leftLimit = 0;
		int rightLimit = machine.size();
		float tempTwt = twt;
		
		// Imposto il limite sinistro del range da considerare
		if((position - range) > 0)
		{
			leftLimit = position - range;
		}
		
		// Imposto il limite destro del range da considerare
		if((position + range) < rightLimit)
		{
			rightLimit = position + range;
		}
		
		//System.out.println("Limite sinistro: "+leftLimit);
		//System.out.println("Limite destro: "+rightLimit);
		
		// assume valore positivo solo quando trovo una combinazione che migliora il twt. indica la posizione
		// ove devo spostare il job considerato per  ottenere il miglioramento.
		int swapPosition = -1;
		
		// Provo tutte le combinazioni possibili nei limiti appena calcolati
		for(int x = leftLimit; x < rightLimit + 1; x++)
		{
			if(x != position && x < machine.size())
			{
				Job substitutedJob = machine.get(x);
				machine.set(position, substitutedJob);
				machine.set(x, consideredJob);
				float newTwt = calculateTwt();
				if(newTwt < tempTwt) // manca condizione di validità in base alla priorit�
				{
					swapPosition = x;
					//System.out.println("Trovato un miglioramento");
					tempTwt = newTwt;
				}
				machine.set(position, consideredJob);
				machine.set(x, substitutedJob);
			}
		}
		
		if(swapPosition != -1)
		{
			Job j = machine.get(swapPosition);
			machine.set(swapPosition, consideredJob);
			machine.set(position, j);
			twt = tempTwt;
			//printResult();
		}
	}
	
	public void TransferOnOneMachine(int range, int repeat, ArrayList<Job> machine, int position)
	{
		if(range != 0)
		{
			//aggiungere le ripetizioni e il calcolo del twt
			int leftLimit = 0;
			int rightLimit = machine.size() - 1;
			ArrayList<Job> result = new ArrayList<Job>();
			
			// Imposto il limite sinistro del range da considerare e copio la sottolista se alcune posizioni
			// rimarranno invariate rispetto alla mossa perch� il range � troppo piccolo
			if((position - range) > 0)
			{
				leftLimit = position - range;
			}
			
			// Imposto il limite destro del range da considerare
			if((position + range) < rightLimit)
			{
				rightLimit = position + range;
			}
			
			
			for(int y = leftLimit; y < rightLimit + 1; y++)
			{
				String s = "";
				
				// Aggiungo tutti i job che sono fuori dal range considerato
				for(int x = 0; x < leftLimit; x++)
				{
					result.add(machine.get(x));
					s = s + machine.get(x).getName() + "; ";
					System.out.println("Aggiungo alla lista il job: "+machine.get(x).getName());
				}
				System.out.println("Ho aggiunto quelle da non toccare");
				System.out.println("LeftLimit: "+leftLimit);
				System.out.println("rightLimit: "+rightLimit);
				
				// creo tutte le combinazioni del range considerato.
				for(int w = leftLimit; w < rightLimit+1; w++)
				{
					if(w == y)
					{
						result.add(machine.get(position));
						s = s + machine.get(position).getName() + "; ";
						System.out.println("5. Aggiungo alla lista il job: "+machine.get(position).getName()+", quello di interesse.");
					}
					else if(w < y)
					{
						if(w < position)
						{
							result.add(machine.get(w));
							s = s + machine.get(w).getName() + "; ";
							System.out.println("4. Aggiungo alla lista il job: "+machine.get(w).getName());
						}
						else
						{
							result.add(machine.get(w+1));
							s = s + machine.get(w+1).getName() + "; ";
							System.out.println("3. Aggiungo alla lista il job: "+machine.get(w+1).getName());
						}
					}
					else
					{
						if(w <= position)
						{
							result.add(machine.get(w - 1));
							s = s + machine.get(w - 1).getName() + "; ";
							System.out.println("2. Aggiungo alla lista il job: "+machine.get(w - 1).getName());
						}
						else
						{
							result.add(machine.get(w));
							s = s + machine.get(w).getName() + "; ";
							System.out.println("1. Aggiungo alla lista il job: "+machine.get(w).getName());
						}
					}
				}
				
				for(int z = rightLimit+1; z < machine.size(); z++)
				{
					result.add(machine.get(z));
					s = s + machine.get(z).getName() + "; ";
					System.out.println("6. Aggiungo alla lista il job: "+machine.get(z).getName());
				}
				
				System.out.println("risultato: "+s);
				System.out.println("");
			}
		}
	}

	public void SwapAcrossMachines(int range, int repeat, ArrayList<Job> originalMachine, int position)
	{
		Job consideredJob = originalMachine.get(position);
		int leftLimit = 0;
		int rightLimit = 0;
		int swapPosition = -1;
		int machineIndex = -1;
		int index = allMachines.indexOf(originalMachine);
		float tempTwt = twt;
		
		for(int x = 0; x < allMachines.size(); x++)
		{
			if(x != index)
			{
				ArrayList<Job> machine = allMachines.get(x);
				
				// Se una macchina ha meno posizioni di quella considerata lo swap non viene effettuato.
				if(machine.size() < position)
				{
					System.out.println("Swap non effettuabile.");
					continue;
				}
				
				rightLimit = machine.size() - 1;
				
				// Imposto il limite sinistro del range da considerare
				if((position - range) > 0)
				{
					leftLimit = position - range;
				}
				
				// Imposto il limite destro del range da considerare
				if((position + range) < rightLimit)
				{
					rightLimit = position + range;
				}
				
				for(int y = leftLimit; y < rightLimit + 1; y++)
				{
					Job substitutedJob = machine.get(y);
					machine.set(y, consideredJob);
					originalMachine.set(position, substitutedJob);
					float newTwt = calculateTwt();
					System.out.println("Tentativo:");
					printResult();
					System.out.println("TWT: "+newTwt);
					System.out.println("Fine tentativo.");
					if(newTwt < tempTwt) // manca condizione di validit� in base alla priorit�
					{
						swapPosition = y;
						machineIndex = x;
						System.out.println("Trovato un miglioramento con swapAcrossMachines.");
						tempTwt = newTwt;
					}
					machine.set(y, substitutedJob);
					originalMachine.set(position, consideredJob);
				}
			}
		}
		
		if(machineIndex != -1)
		{
			Job j = originalMachine.get(position);
			Job j2 = allMachines.get(machineIndex).get(swapPosition);
			allMachines.get(machineIndex).set(swapPosition, j);
			allMachines.get(machineIndex).set(index, j2);
		}
		
		printResult();
		
	}
	
	public void TransferAcrossMachines(int range, int repeat, ArrayList<Job> originalMachine, int position)
	{
		ArrayList<Job> result = null;
		int index = allMachines.indexOf(originalMachine);
		String s = "";
		int rightLimit = 0;
		int leftLimit = 0;
		
		// Parametri che salvano la miglior mossa
		float tempTwt = twt;
		ArrayList<Job> o = new ArrayList<Job>(); //originalMachine privata dell'elemento position
		ArrayList<Job> res = new ArrayList<Job>(); //altra macchina modificata
		int machineNumber = -1; //indice della macchina modificata
		
		for(int x = 0; x < allMachines.size(); x++)
		{
			if(x != index)
			{
				ArrayList<Job> machine = allMachines.get(x);
				
				if(machine.size() - 1 < position)
				{
					// posiziono il job in fonod alla macchina e provo il calcolo.
					continue;
				}
				
				rightLimit = machine.size() - 1;
				
				// Imposto il limite sinistro del range da considerare
				if((position - range) > 0)
				{
					leftLimit = position - range;
				}
				
				// Imposto il limite destro del range da considerare
				if((position + range) < rightLimit)
				{
					rightLimit = position + range;
				}
				
				
				// Considero tutti i possibili spostamenti
				for(int y = leftLimit; y < rightLimit + 1; y++)
				{
					result = new ArrayList<Job>();
					int j = 0;
					
					while(j < y)
					{
						result.add(machine.get(j));
						//System.out.println("0. Aggiungo alla lista il job: "+machine.get(j).getName());
						j++;
					}
					
					result.add(originalMachine.get(position));
					
					while(j < machine.size())
					{
						result.add(machine.get(j));
						//System.out.println("1. Aggiungo alla lista il job: "+machine.get(j).getName());
						j++;
					}
					
					System.out.println("Tentativo: ");
					String sa = "";
					for(int f = 0 ; f < result.size(); f++)
					{
						sa = sa + result.get(f).getName()+" ,";
					}
					System.out.println(sa);
					
					// Salvo la macchina in cui inserisco il job cos� com'� PRIMA della modifica
					ArrayList<Job> temp = allMachines.get(x);
					allMachines.set(x, result);
					// Salvo la macchina originale cos� com'� PRIMA della modifica
					ArrayList<Job> orig = allMachines.get(index);
					// Creo la macchina originale NUOVA
					ArrayList<Job> a = new ArrayList<Job>();
					for(int k = 0; k < orig.size(); k++)
					{
						if(k != position)
						{
							a.add(orig.get(k));
						}
					}
					// Inserisco temporaneamente la macchina originale nuova
					allMachines.set(index, a);
					float t = calculateTwt();
					System.out.println("TWT: "+t);
					if(t < tempTwt)
					{
						o.clear();
						res.clear();
						System.out.println("Miglioramento trovato");
						tempTwt = t;
						machineNumber = x;
						String sb = "";
						// Salvo la macchina original temporanea (quella inserita ora)
						for(int k = 0; k < a.size(); k++)
						{
								o.add(a.get(k));
								sb = sb + orig.get(k).getName() + ", ";
						}
						System.out.println("Ordine macchina originale: "+sb);
						String sc = "";
						// Salvo l'altra macchina (com'� ora)
						for(int k = 0; k < result.size(); k++)
						{
								res.add(result.get(k));
								sc = sc + result.get(k).getName()+", ";
						}
					}
					// Rimetto la macchina originale com'era prima della modifica
					allMachines.set(index, orig);
					// Rimetto la macchina di destinazione comera prima della modifica
					allMachines.set(x, temp);
				}
			}
		}
		
		if(machineNumber != -1)
		{
			allMachines.set(index, o);
			allMachines.set(machineNumber, res);
			System.out.println("Risultato finale: ");
			printResult();
			System.out.println("TWT: ");
			System.out.println(calculateTwt());
		}
		
		
		
	}
	// Metodi temporanei per il funzionamento provvisorio
	
	
	public float calculateTwt()
	{
		// TODO questa funzione calcola il twt anche se è già stato calcolato
		//      è possibile che il twt non cambi tra due chiamate o cambia per forza?
		
		float resultingTwt = 0;
		for(int a = 0; a < allMachines.size(); a++)
		{
			ArrayList<Job> machine = allMachines.get(a);
			//System.out.println("Calcolo il twt su: "+machine.size()+" job");
			Job j = null;
			float time = 0;
			for(int b = 0; b < machine.size(); b++)
			{
				// Calcolo il tempo in cui termina il job
				j = machine.get(b);
				time = time + j.getExecutionTime();
				if(time > j.getDueDate())
				{
					float paid = (time - j.getDueDate())*j.getWeight();
					resultingTwt = resultingTwt + paid;
				}
			}
		}
		//Main.log("TWT = " + resultingTwt);
		return resultingTwt;
	}
	
	public void daRimuovere()
	{
		twt = calculateTwt();
	}
	
	public void printResult()
	{
		System.out.println("");
		System.out.println("ORDINE DELLE MACCHINE:");
		for(int x = 0; x < allMachines.size(); x++)
		{
			ArrayList<Job> alj = allMachines.get(x);
			System.out.println("Macchina #"+x);
			String s = "";
			for(int y = 0; y < alj.size(); y++)
			{
				s = s + alj.get(y).getName() + ", ";
			}
			System.out.println(s);
		}
		System.out.println("FINE");
		System.out.println("");
	}
	
	/**
	 * @param jobArray ArrayList dei job in ordine sparso
	 */
	public void inizializzaCoiJob(ArrayList<Job> jobArray) {
		// just put all the jobs on a machine for now
		for (Job job : jobArray) {
			addJobOnMachine(0, job);
		}
		twt = calculateTwt();
	}

	/**
	 * @param k il numero del neighborhood da esplorare
	 * @return uno <b>StorageVNS</b> con la nuova soluzione 
	 */
	public StorageVNS muoviCasualmenteNelNeighborhood(int k) {
		// TODO crea una deep-copy e la muove, meglio sarebbe avere sempre
		//      due soluzioni e avere delle mosse che si possono annullare
		//      in modo da non dover sempre istanziare nuove classi (quindi
		//      ad esempio fare una mossa sulla soluzione 2 e se non porta
		//      a niente di buono annullare solo la mossa e non buttare via
		//      tutta la classe
		StorageVNS nuovaSoluzione = new StorageVNS(this);
		
		// parametro r è il range della mossa
		int range;
		// parametro l è il numero di ripetizioni della mossa
		int repeat;
		// parametro che indica il codice della mossa
		int moveCode; Method move = null;
		
		moveCode = k % 4; // ci sono solo 4 mosse numerate da 0 a 3
		range = ((int) (Math.floor(k / 4))) % 4; // ci sono solo 4 possibilita [2,5,10,n_max]
		repeat = ((int)  (Math.floor(k/16))) % 3; // ci sono solo 3 possibilità [1,2,3]
		
		if (range == 0) range = 2;
		if (range == 1) range = 5;
		if (range == 2) range = 10;
		if (range == 3) range = 0; // nella funzione vale come nmax
		
		try {
			if (moveCode == 0) move = this.getClass().getMethod("swapOnOneMachine", int.class, int.class);
			if (moveCode == 1) move = this.getClass().getMethod("swapOnOneMachine", int.class, int.class);
			if (moveCode == 2) move = this.getClass().getMethod("swapOnOneMachine", int.class, int.class);
			if (moveCode == 3) move = this.getClass().getMethod("swapOnOneMachine", int.class, int.class);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO c’è qualcosa che non va se finisce qui
			Main.log("la riflessività non ha funzionato");
			e.printStackTrace();
			System.exit(1);
		}
		
		try {
			move.invoke(nuovaSoluzione, range, repeat);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO c’è qualcosa che non va se finisce qui
			Main.log("la riflessività non ha funzionato nemmeno qui");
			e.printStackTrace();
			System.exit(1);
		}
		
		return nuovaSoluzione;
	}

	public String toString() {
		return allMachines.toString() + "\nCosto soluzione finale = " + this.calculateTwt();
	}
}
