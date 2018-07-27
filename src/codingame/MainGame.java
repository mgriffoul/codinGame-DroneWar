import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

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
        int numberOfDronePerTeam = D;
        int numberOfZone = Z;

        List<Zone> zonesInGame = new ArrayList<>();
        List<DronePlayer> dronePlayerList;
        DronePlayer me = new DronePlayer();

        for (int i = 0; i < numberOfZone; i++) {
            int X = in.nextInt(); // corresponds to the position of the center of a zone. A zone is a circle with a radius of 100 units.
            int Y = in.nextInt();
            //Récupération des zones de la partie
            Zone zone = new Zone(X, Y);
            zone.setId(i);
            zonesInGame.add(zone);
        }

        // game loop
        while (true) {

            //Réinitialisation des collections pour chaque tours
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
                    drone.setOwner(dronePlayer);
                    droneList.add(drone);
                    dronePlayer.setDrones(droneList);


                    drone.setZonesByDistance(getZonesSortedByDistance(drone, zonesInGame));

                }

                if(ID == i){
                    me = dronePlayer;
                }

            }

            System.err.println("more points " + whosGettingMorePoints(zonesInGame, dronePlayerList));

            affecterTarget(zonesInGame, me.getDrones(), numberOfZone);

            for (Drone d : dronePlayerList.get(me.playerId).getDrones()) {

                System.out.println(d.initialTarget.getCoord());

            }

        }
    }


    static int whosGettingMorePoints (List<Zone> zonesInGame, List<DronePlayer> dronePlayerList){

        System.err.println("size drone "+ dronePlayerList.size());
        System.err.println("size zone "+ zonesInGame.size());

        Map<Integer, Integer> playerScorePlayerIdTreeMap = new TreeMap<>();

        dronePlayerList.stream().forEach(
                player -> playerScorePlayerIdTreeMap.put(
                                (int)zonesInGame.stream().filter(zone -> zone.getControllingPlayerId() != player.getPlayerId()).count(),
                                player.getPlayerId()));

        System.err.println("size "+ playerScorePlayerIdTreeMap.size());

        if(playerScorePlayerIdTreeMap.isEmpty()){
            return -1;
        }else {
            return ((TreeMap<Integer, Integer>) playerScorePlayerIdTreeMap).firstEntry().getValue();
        }
    }

    static void affecterTarget(List<Zone> zones, List<Drone> drones, int numberOfZone) {

        for (int i = 0; i < drones.size(); i++) {
            Drone d = drones.get(i);
            int zoneId = d.getZonesByDistance().get(0);
            Zone zoneToTarget = zones.get(zoneId);
            d.setInitialTarget(zoneToTarget);
        }

    }


    //Renvoie, pour chaque drone, la liste des zones par ordre croissant d'éloignement
    static List<Integer> getZonesSortedByDistance(Drone drone, List<Zone> zones) {
        TreeMap<Integer, Integer> zoneDistanceZoneIndexTreeMap = new TreeMap<>();
        zones.stream().forEach(z -> zoneDistanceZoneIndexTreeMap.put(calculateDistance(drone, z), z.getId()));
        return zoneDistanceZoneIndexTreeMap.values().stream().collect(Collectors.toList());
    }


    static int calculateDistance(Drone drone, Zone zone) {


        int i = (int) sqrt(
                addExact(
                        (long) (pow((double) subtractExact(drone.getCoordX(), zone.getCoordX()), 2D)),
                        (long) (pow((double) subtractExact(drone.getCoordY(), zone.getCoordY()), 2D))
                )
        );
        return i;
    }


    public static class Zone {

        private int coordX;
        private int coordY;
        private int numberOfDroneOver;
        private int controllingPlayerId;
        private int id;

        public Zone(int coordX, int coordY) {
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

        public String getCoord() {
            return coordX + " " + coordY;
        }

        @Override
        public String toString() {
            return "Zone{" +
                    "coordX=" + coordX +
                    ", coordY=" + coordY +
                    ", numberOfDroneOver=" + numberOfDroneOver +
                    ", controllingPlayerId=" + controllingPlayerId +
                    ", id=" + id +
                    '}';
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
        //List triée par ordre d'éloignement du drone à l'Id des différentes zones
        private List<Integer> zonesByDistance;
        private Zone initialTarget;

        public Drone(int coordX, int coordY) {
            this.coordX = coordX;
            this.coordY = coordY;
        }

        public Zone getInitialTarget() {
            return initialTarget;
        }

        public void setInitialTarget(Zone initialTarget) {
            this.initialTarget = initialTarget;
        }

        public List<Integer> getZonesByDistance() {
            return zonesByDistance;
        }

        public void setZonesByDistance(List<Integer> zonesByDistance) {
            this.zonesByDistance = zonesByDistance;
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

        @Override
        public String toString() {

            String zoneByDistance = "";
            if(this.zonesByDistance != null){
                for (Integer i : this.zonesByDistance){
                    zoneByDistance+= i.toString() + " / ";
                }
            }


            return  "id = " + this.id
                    + " coordX = " + this.coordX
                    + "coordY = " + this.coordY
                    + "owner = " + this.owner.getPlayerId()
                    + " ZoneByDistance =  " + zoneByDistance;
        }
    }

}