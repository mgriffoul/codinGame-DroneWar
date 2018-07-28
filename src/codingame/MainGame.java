

import java.util.*;
import java.util.function.Predicate;
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


                    drone.setZonesByDistance(getTargetZonesSortedByDistance(drone, zonesInGame, ID));

                }

                if (ID == i) {
                    me = dronePlayer;
                }

            }

            analyseDronePositionOverZone(zonesInGame, dronePlayerList);
            defenseFlagAffecter(zonesInGame, me.getDrones(),ID);

            System.err.println("more points " + whosGettingMorePoints(zonesInGame, dronePlayerList));

            targetAffecter(zonesInGame, me.getDrones());

            for (Drone d : dronePlayerList.get(me.playerId).getDrones()) {

                System.out.println(d.initialTarget.getCoord());
            }

        }
    }

    //changer en java 8 Set la liste des drone au dessus d'une zone par id de joueur
    static void analyseDronePositionOverZone(List<Zone> zones, List<DronePlayer> dronePlayerList) {
        for (Zone z : zones) {
            for (DronePlayer d : dronePlayerList) {
                for (Drone drone : d.getDrones()) {
                    if ((z.getCoordX() - 100 < drone.getCoordX() && z.getCoordX() + 100 > drone.getCoordX())
                            && (z.getCoordY() - 100 < drone.getCoordY() && z.getCoordY() + 100 > drone.getCoordY())) {

                        z.getPlayerIdNumberOfDroneOver().put(d.getPlayerId(), drone.getId());
                        drone.setFlyingOverZone(z);
                    }
                }
            }
        }
    }


    static int whosGettingMorePoints(List<Zone> zonesInGame, List<DronePlayer> dronePlayerList) {
        Map<Integer, Integer> playerScorePlayerIdTreeMap = new TreeMap<>();

        dronePlayerList.stream().forEach(
                player -> playerScorePlayerIdTreeMap.put(
                        (int) zonesInGame.stream().filter(zone -> zone.getControllingPlayerId() != player.getPlayerId()).count(),
                        player.getPlayerId()));

        System.err.println("size " + playerScorePlayerIdTreeMap.size());

        if (playerScorePlayerIdTreeMap.isEmpty()) {
            return -1;
        } else {
            return ((TreeMap<Integer, Integer>) playerScorePlayerIdTreeMap).firstEntry().getValue();
        }
    }


    static void targetAffecter(List<Zone> zones, List<Drone> myDrones) {

        myDrones.stream().forEach(drone -> {
            if (drone.getZonesByDistance() != null && !drone.getZonesByDistance().isEmpty()) {
                drone.setInitialTarget(zones.get(drone.getZonesByDistance().get(0)));
            } else {
                drone.setInitialTarget(new Zone(drone.getCoordX(), drone.getCoordY()));
            }
        });

    }


    static void defenseFlagAffecter(List<Zone> zones, List<Drone> myDrones, int myId) {
        setDefendFlag(zones,
                myDrones.stream().filter(drone -> drone.getFlyingOverZone() != null
                        && drone.getFlyingOverZone().numberOfDroneOver > 1
                        && drone.getFlyingOverZone().getControllingPlayerId() != myId).collect(Collectors.toList()),
                myId);
    }


    static boolean setDefendFlag(List<Zone> zones, List<Drone> myFilteredDrones, int myId) {

        for (Zone z : zones) {
            int defenderCount = 0;
            int myDronesInZone = z.getPlayerIdNumberOfDroneOver().get(myId);
            int maxEnnemyDrone = z.getPlayerIdNumberOfDroneOver()
                    .values()
                    .stream()
                    .collect(Collectors.summarizingInt(Integer::intValue))
                    .getMax();

            boolean needSomeDefenders = false;

            for(int i : z.getPlayerIdNumberOfDroneOver().values()){
                if(myDronesInZone >= i){
                    needSomeDefenders = true;
                }
            }

            if(needSomeDefenders){
                for (Drone myDrone : myFilteredDrones){

                    if(defenderCount <= maxEnnemyDrone){
                        myDrone.setDefendingZone(true);
                        defenderCount++;
                    }

                }
            }

        }

    }

    static Predicate<Drone> test(int myId) {
        return drone -> drone.


    }


    //Renvoie, pour chaque drone, la liste des zones par ordre croissant d'éloignement. On retire à la volé les zones déjà controllées.
    static List<Integer> getTargetZonesSortedByDistance(Drone drone, List<Zone> zones, int myId) {
        TreeMap<Integer, Integer> zoneDistanceZoneIndexTreeMap = new TreeMap<>();
        substractSafeZone(zones, myId).stream().forEach(z -> zoneDistanceZoneIndexTreeMap.put(calculateDistance(drone, z), z.getId()));
        return zoneDistanceZoneIndexTreeMap.values().stream().collect(Collectors.toList());
    }


    static List<Zone> substractSafeZone(List<Zone> zonesSortedByDistance, int myId) {
        return zonesSortedByDistance.stream().filter(ZoneInDanger(myId)).collect(Collectors.toList());
    }

    static Predicate<Zone> ZoneInDanger(int myId) {
        return z -> z.getControllingPlayerId() != myId;
    }


    static int calculateDistance(Drone drone, Zone zone) {
        return (int) sqrt(
                addExact(
                        (long) (pow((double) subtractExact(drone.getCoordX(), zone.getCoordX()), 2D)),
                        (long) (pow((double) subtractExact(drone.getCoordY(), zone.getCoordY()), 2D))
                )
        );
    }


    public static class Zone {

        private int coordX;
        private int coordY;
        private int numberOfDroneOver;
        private Map<Integer, Integer> playerIdNumberOfDroneOver;
        private int controllingPlayerId;
        private int id;

        public Zone(int coordX, int coordY) {
            this.coordX = coordX;
            this.coordY = coordY;
            this.playerIdNumberOfDroneOver = new HashMap<>();
        }

        Map<Integer, Integer> getPlayerIdNumberOfDroneOver() {
            return playerIdNumberOfDroneOver;
        }

        public void setPlayerIdNumberOfDroneOver(Map<Integer, Integer> playerIdNumberOfDroneOver) {
            this.playerIdNumberOfDroneOver = playerIdNumberOfDroneOver;
        }

        int getId() {
            return id;
        }

        void setId(int id) {
            this.id = id;
        }

        int getCoordX() {
            return coordX;
        }

        void setCoordX(int coordX) {
            this.coordX = coordX;
        }

        int getCoordY() {
            return coordY;
        }

        void setCoordY(int coordY) {
            this.coordY = coordY;
        }

        int getNumberOfDroneOver() {
            return numberOfDroneOver;
        }

        void setNumberOfDroneOver(int numberOfDroneOver) {
            this.numberOfDroneOver = numberOfDroneOver;
        }

        int getControllingPlayerId() {
            return controllingPlayerId;
        }

        void setControllingPlayerId(int controllingPlayerId) {
            this.controllingPlayerId = controllingPlayerId;
        }

        String getCoord() {
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

    static class DronePlayer {

        private int playerId;
        private List<Drone> drones;

        DronePlayer() {
        }

        DronePlayer(int playerId) {
            this.playerId = playerId;
        }

        int getPlayerId() {
            return playerId;
        }

        void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        List<Drone> getDrones() {
            return drones;
        }

        void setDrones(List<Drone> drones) {
            this.drones = drones;
        }
    }

    static class Drone {

        private int id;
        private int coordX;
        private int coordY;
        private DronePlayer owner;
        //List triée par ordre d'éloignement du drone à l'Id des différentes zones
        private List<Integer> zonesByDistance;
        private Zone initialTarget;
        private Zone flyingOverZone;
        private boolean defendingZone;

        Drone(int coordX, int coordY) {
            this.coordX = coordX;
            this.coordY = coordY;
        }

        boolean isDefendingZone() {
            return defendingZone;
        }

        void setDefendingZone(boolean defendingZone) {
            this.defendingZone = defendingZone;
        }

        Zone getFlyingOverZone() {
            return flyingOverZone;
        }

        void setFlyingOverZone(Zone flyingOverZone) {
            this.flyingOverZone = flyingOverZone;
        }

        Zone getInitialTarget() {
            return initialTarget;
        }

        void setInitialTarget(Zone initialTarget) {
            this.initialTarget = initialTarget;
        }

        List<Integer> getZonesByDistance() {
            return zonesByDistance;
        }

        void setZonesByDistance(List<Integer> zonesByDistance) {
            this.zonesByDistance = zonesByDistance;
        }

        int getId() {
            return id;
        }

        void setId(int id) {
            this.id = id;
        }

        int getCoordX() {
            return coordX;
        }

        void setCoordX(int coordX) {
            this.coordX = coordX;
        }

        int getCoordY() {
            return coordY;
        }

        void setCoordY(int coordY) {
            this.coordY = coordY;
        }

        DronePlayer getOwner() {
            return owner;
        }

        void setOwner(DronePlayer owner) {
            this.owner = owner;
        }

        @Override
        public String toString() {

            String zoneByDistance = "";
            if (this.zonesByDistance != null) {
                for (Integer i : this.zonesByDistance) {
                    zoneByDistance += i.toString() + " / ";
                }
            }


            return "id = " + this.id
                    + " coordX = " + this.coordX
                    + "coordY = " + this.coordY
                    + "owner = " + this.owner.getPlayerId()
                    + " ZoneByDistance =  " + zoneByDistance;
        }
    }

}