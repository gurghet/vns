vns
===

Implementazione di “Variable neighborhood search approaches for scheduling jobs on parallel machines with sequence-dependent setup times, precedence constraints, and ready times”

    Find an initial solution s ;
    while maximum runtime not reached do
    	k ←1;
    	While k≤kmax do
    		Shaking:selectarandomsolution x∈Nk(s);
    			Apply some local search with x as initial
    			solution, local optimum x′
    		Move or not:
    		if solution x′ is better than s then
    			s ← x′; k ← 1;
    		else
    			k ← kmodkmax+1;
    	End
    End
