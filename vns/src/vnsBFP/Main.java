package vnsBFP;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

	/**
	 * @param nome file istanza, numero macchine, numero mosse
	 */
	public static void main(String[] args) {
		// leggi il file istanza e crea gli oggetti Jobs
		// inserendoli man mano nella macchina dummy
		// nell'ordine in cui vengono letti
		
		// fai girare l'algoritmo

		// stampa risultato su un file
	}
	
	private Machine getDummyMachine() {
		Machine dummyMachine = new Machine();
		List<String> lines;
		Path path = Paths.get("somepath");
		return dummyMachine;
	}

}