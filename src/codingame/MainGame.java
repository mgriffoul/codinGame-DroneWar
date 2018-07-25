import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int P = in.nextInt(); // number of players in the game (2 to 4 players)
        int ID = in.nextInt(); // ID of your player (0, 1, 2, or 3)
        int D = in.nextInt(); // number of drones in each team (3 to 11)
        int Z = in.nextInt(); // number of zones on the map (4 to 8)

        int numberOfPlayer = P;
        int myId = ID;
        int numberOfDronePerTeam = D;
        int numberOfZone = Z;

        List<Zone> zonesInGame = new ArrayList<>();
        List<DronePlayer> dronePlayerList;
        Map<Integer, Drone> dronesMap;


        for (int i = 0; i < numberOfZone; i++) {
            int X = in.nextInt(); // corresponds to the position of the center of a zone. A zone is a circle with a radius of 100 units.
            int Y = in.nextInt();
            //Récupération des zones de la partie
            Zone zone = new Zone(X, Y);
            zonesInGame.add(zone);
        }

        // game loop
        while (true) {

            //Réinitialisation des collections pour chaque tours
            dronesMap = new HashMap<>();
            dronePlayerList = new ArrayList<>();

            for (int i = 0; i < numberOfZone; i++) {
                int TID = in.nextInt(); // ID of the team controlling the zone (0, 1, 2, or 3) or -1 if it is not controlled. The zones are given in the same order as in the initialization.
                //Recupération du joueur qui controle la zone
                zonesInGame.get(i).setControllingPlayerId(TID);
            }

            for (int i = 0; i < numberOfPlayer; i++) {
                //Récupération des joueurs de la partie
                DronePlayer dronePlayer = new DronePlayer(i);
                dronePlayerList.add(dronePlayer);

                //Traitement des drones
                List<Drone> droneList = new ArrayList<>();
                for (int j = 0; j < numberOfDronePerTeam; j++) {
                    int coordX = in.nextInt(); // The first D lines contain the coordinates of drones of a player with the ID 0, the following D lines those of the drones of player 1, and thus it continues until the last player.
                    int coordY = in.nextInt();

                    //Récupération des drones de la partie et assignation aux joueurs
                    Drone drone = new Drone(coordX, coordY);
                    droneList.add(drone);
                    dronePlayer.setDrones(droneList);
                    dronesMap.put(i, drone);

                }
            }

            int indexOfZone = 0;
            for (int i = 0; i < numberOfDronePerTeam; i++) {

                System.err.println("drone index avant" + i);
                System.err.println("index of zone avant"+indexOfZone);
                System.err.println("coord to go avant" + zonesInGame.get(indexOfZone).getCoord());

                System.out.println(zonesInGame.get(indexOfZone).getCoord());

                indexOfZone = (indexOfZone == numberOfZone - 1) ?  0 : indexOfZone+1;

                System.err.println("drone index apres" + i);
                System.err.println("index of zone apres"+indexOfZone);
                System.err.println("coord to go apres" + zonesInGame.get(indexOfZone).getCoord());

            }

        }
    }

    public static class Zone {
        private int coordX;
        private int coordY;
        private int numberOfDroneOver;
        private int controllingPlayerId;

        public Zone(int coordX, int coordY) {
            this.coordX = coordX;
            this.coordY = coordY;
        }

        public int getCoordX() {
            return coordX;
        }

        public void setCoordX(int coordX) {
            this.coordX = coordX;
        }

        public int getCoordY() {
            return coordY;
        }

        public void setCoordY(int coordY) {
            this.coordY = coordY;
        }

        public int getNumberOfDroneOver() {
            return numberOfDroneOver;
        }

        public void setNumberOfDroneOver(int numberOfDroneOver) {
            this.numberOfDroneOver = numberOfDroneOver;
        }

        public int getControllingPlayerId() {
            return controllingPlayerId;
        }

        public void setControllingPlayerId(int controllingPlayerId) {
            this.controllingPlayerId = controllingPlayerId;
        }

        public String getCoord(){
            return coordX+" "+coordY;
        }

    }

    public static class DronePlayer {
        private int playerId;
        private List<Drone> drones;

        public DronePlayer() {
        }

        public DronePlayer(int playerId) {
            this.playerId = playerId;
        }

        public int getPlayerId() {
            return playerId;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        public List<Drone> getDrones() {
            return drones;
        }

        public void setDrones(List<Drone> drones) {
            this.drones = drones;
        }
    }

    public static class Drone {
        private int id;
        private int coordX;
        private int coordY;
        private DronePlayer owner;

        public Drone(int coordX, int coordY) {
            this.coordX = coordX;
            this.coordY = coordY;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCoordX() {
            return coordX;
        }

        public void setCoordX(int coordX) {
            this.coordX = coordX;
        }

        public int getCoordY() {
            return coordY;
        }

        public void setCoordY(int coordY) {
            this.coordY = coordY;
        }

        public DronePlayer getOwner() {
            return owner;
        }

        public void setOwner(DronePlayer owner) {
            this.owner = owner;
        }
    }

}