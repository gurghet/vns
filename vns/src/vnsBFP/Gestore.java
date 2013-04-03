package vnsBFP;

import java.util.ArrayList;
import java.util.List;

public class Gestore {
	/**
	 * Lista delle macchine che schedulano i job
	 */
	public List<Machine> macchine;
	
	/**
	 * @param numMacchine il numero di macchine che schedulano i job
	 */
	public Gestore(int numMacchine) {
		macchine = new ArrayList<Machine>(numMacchine);
	}
}
