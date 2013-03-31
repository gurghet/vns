package vnsBFP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

	/**
	 * @param nome file istanza, numero macchine, numero mosse
	 */
	public static void main(String[] args) {
		Machine dummyMachine = getDummyMachine();
		// leggi il file istanza e crea gli oggetti Jobs
		// inserendoli man mano nella macchina dummy
		// nell'ordine in cui vengono letti
		
		// fai girare l'algoritmo

		// stampa risultato su un file
	}
	
	private static Machine getDummyMachine() {
		Machine dummyMachine = new Machine();
		List<String> lines = null;
		// mi sa che la classe Pash e' nuova della Java jdk 1.7
		// se non l'avete si trova sul sito della oracle
		Path path = Paths.get("prova.txt");
		try {
			lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    lines.add("This is a line added in code.");
	    System.out.println(String.valueOf(lines));
		return dummyMachine;
	}

}