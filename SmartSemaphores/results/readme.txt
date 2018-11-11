Simulation reports are saved here. There are a total of 6 reports per simulation, all in the same folder.
Each simulation execution has an unique ID to tell it apart from the others (UNIX timestamp on the moment it ends).

Reports:
- <id>_parameters			parameters used for the simulation
- <id>_avg_time_emergency.csv		average time for emergency vehicles that followed the same route
- <id>_avg_time_normal.csv		average time for normal vehicles that followed the same route
- <id>_total_time_emergency.csv		individual time of each emergency vehicle that exited the network
- <id>_total_time_normal.csv		individual time of each normal vehicle that exited the network
- <id>_semaphores_state.csv		state of each semaphore at each tick of the simulation