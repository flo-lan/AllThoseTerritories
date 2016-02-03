enum Phase {
    Setup,      //Put units on territories
    Reinforce,  //Place units at the start of a round
    Conquest,   //Attack and Move units (after inital setup)
    Attack,     //From Conquest, if you start to attack somebody
    Attacked,   //After Attack you can get all rest units from the attack with right click
    Move,        //Move units, if you move units, you cant attack
    End
}

enum Player {
    None,
    Human,
    Bot
}