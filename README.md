# Elevator Simulation

In this Elevator Simulation project, as a group, we simulated the performance of various elevators currently being proposed for a new 6-story building. The simulation provided data that was analyzed and then used to make a recommendation to the lead architect.
This is a time-driven and event-driven simulation. The elevator functionality was modeled using a finite state machine (FSM), and passengers arrived at specified times with intended destinations – just like they would in a real building.

## Approaching the Elevator Project with a Design Doc:

To complete the project successfully, we decided to create and fully flush out a design document to communicate necessary variables, methods, getters, and setters. Furthermore, each class was a separate document, with each method header written out with an executive-level summary of the function. This process was done to ensure each member working on their designated files would not need to access other files, to ensure there were no issues when merging into the main branch.

## In depth-look at the Elevator Project GUI:
#### GUI Mockup (Version 1 → Final Version)

First GUI Mockup (Week 1 of the project)
<p align="center">
  <image src="https://github.com/Hy8012/ElevatorSimulation/blob/main/md_files/first_design_doc.png?raw=true" width="600" height ="340"/>
</p>

Final Design of GUI
* Arrows represent the direction of the Elevator
* The blue box represents the elevator itself, and the number represents the # of passengers
* Toggleable buttons added to the bottom

<p align="center">
  <image src="https://github.com/Hy8012/ElevatorSimulation/blob/main/md_files/final_GUI.png?raw=true" width="600" height="470"/>
</p>

## High-Level class UML Layout:

<p align="center">
  <image src="https://github.com/Hy8012/ElevatorSimulation/blob/main/md_files/UML.png?raw=true" width="1170" height="750"/>
</p>

# Comprehensive  JUnit Testing:
#### One of the numerous test cases for passing the JUnit. The one below is the Move 1 Floor Test (Mv1FlrTest.csv)

<p align="center"/>
  <image src="https://github.com/Hy8012/ElevatorSimulation/blob/main/md_files/JUnit_CSV1_test.png?raw=true" width="1170" height="1360"/>
</p>
    
# Final Elevator Simulation:

Fully interactive, user-friendly, GUI, that correctly shows the Elevator Simulation.

Added toggleable logging option, printing out detailed statistics every time an action occurs.

All 3 Elevator Config simulations fully passed (Used to recommend the best elevator to the architect).







