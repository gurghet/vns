package vnsBFP;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Andrea Passaglia
 * @author Paolo Fontanelli
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
		// questi puoi settarli nell’IDE così:
		// 
		String filePath = args[0];
		int numMacchine = Integer.parseInt(args[1]);
		int maxIterations = Integer.parseInt(args[2]);
		
		// leggi il file istanza e crea gli oggetti Jobs
		// inserendoli man mano nella macchina dummy
		// nell'ordine in cui vengono letti
		Machine dummyMachine = getDummyMachine(filePath);
		
		// crea le macchine previste dal run
		for (int i = 0; i < numMacchine; i++){
			
		}
		
		// schedula i lavori secondo la soluzione iniziale
		
		// cuore dell'algoritmo

		// stampa risultato su un file
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
	private static Machine getDummyMachine(String filePath) {
		Machine dummyMachine = new Machine();
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
			Job newJob = new Job();
			newJob.id = i;
			newJob.procTime = Integer.parseInt(jobLine[0]);
			newJob.dueDate = Integer.parseInt(jobLine[1]);
			newJob.weight = Integer.parseInt(jobLine[2]);
			dummyMachine.jobs.add(newJob);
			log(dummyMachine.jobs.get(i));
		}
	    
		return dummyMachine;
	}

}