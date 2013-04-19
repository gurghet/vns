package vnsBFP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Passaglia
 * @version 0.1
 */
public class Main {

	/**
	 * Questa variabile va modificata manualmente, se impostata a true
	 * il programma stampa il log effettuato con la funzione static
	 * log(oggetto) o Main.log(oggetto).
	 */
	private static final boolean VERBOSE = true;
	
	/**
	 * @param args	String[] contenente: nome file istanza, numero macchine, numero mosse
	 */
	public static void main(String[] args) {
		String filePath = null;
		int numMacchine = 1;
		int maxIterations = 100;
		if (args.length == 3) {
			filePath = args[0];
			numMacchine = Integer.parseInt(args[1]);
			maxIterations = Integer.parseInt(args[2]);			
		} else if (args.length == 2) {
			filePath = args[0];
			numMacchine = Integer.parseInt(args[1]);
		} else if (args.length == 1) {
			filePath = args[0];
		}
		if (filePath == null) {
			System.out.println("File istanza di problema non specificato");
			System.exit(0);
		}
		
		// inizializza la classe manager con le macchine previste
		StorageVNS soluzione = new StorageVNS(numMacchine);
		
		// leggi il file istanza e crea gli oggetti Jobs
		// inserendoli man mano nella macchina dummy
		// nell'ordine in cui vengono letti
		ArrayList<Job> jobArray = getJobArray(filePath);
		
		// schedula i lavori secondo la soluzione iniziale s
		soluzione.inizializzaCoiJob(jobArray);
		float twtIniziale = soluzione.calculateTwt();
		
		// cuore dell'algoritmo
		// TODO mettere un timeout
		for (int i = 0; i < maxIterations; i++) {
			log("-- iterazione " + i);
			int k = 0;
			int kmax = 47;
			// while k<=k_{max}
			while (k<=kmax) {
				//log("k=" + k);
				// shaking: select a random solution x'€Nk(s)
				boolean andiamoAvanti = soluzione.muoviCasualmenteNelNeighborhood(k);
				// Move or not:
				// if solution x' is better than s
				if (andiamoAvanti) {
					// s=x'; k=1;
					k = 0;
				} else {
					// k=k%k_{max}+1
					k = k % (kmax + 1) + 1;
				}
			}
		}

		// stampa risultato su un file
		// TODO per ora lo stampa a console
		log(soluzione);
		log("Costo soluzione iniziale: " + twtIniziale);
	}
	
	/**
	 * @param element	Object elemento da stampare se la variabile VERBOSE è true
	 */
	public static void log(Object element) {
		if (VERBOSE) {
			System.out.println(element);
		}
	}
	
	/**
	 * @param filePath 
	 * @return	un oggetto Machine che contiene tutti i job letti nell'istanza
	 * 			senza alcun tipo di schedulazione particolare
	 */
	private static ArrayList<Job> getJobArray(String filePath) {
		ArrayList<Job> jobArray = new ArrayList<Job>();
		List<String> lines = null;
		// mi sa che la classe Pash e' nuova della Java jdk 1.7
		// se non l'avete si trova sul sito della oracle
		Path path = Paths.get(filePath);
		try {
			lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // processing delle linee creazione e aggiunta dei job
	    // la prima linea contiene il numero dei job
	    int nJobs = Integer.parseInt(lines.remove(0).trim());
	    // processo i job
	    String[] jobLine;
	    for (int i = 0; i < nJobs; i++) {
			jobLine = lines.remove(0).trim().split("\\s+");
			int releaseDate = 0;
			int executionTime = Integer.parseInt(jobLine[0]);
			int dueDate = Integer.parseInt(jobLine[1]);
			int weightCost = Integer.parseInt(jobLine[2]);
			Job newJob = new Job(Integer.toString(i), releaseDate, executionTime, dueDate, weightCost);
			jobArray.add(newJob);
			log("Aggiunto il job" + jobArray.get(jobArray.size()-1));
		}
	    
		return jobArray;
	}

}