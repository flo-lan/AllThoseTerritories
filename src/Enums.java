enum Phase {
    Setup,      //Setup: Put units on territories
    Reinforce,  //Reinforce: Place units at the start of a round
    Conquest    //Conquest: Attack and Move units (after inital setup)
}

enum Player {
    None,
    Human,
    Bot
}