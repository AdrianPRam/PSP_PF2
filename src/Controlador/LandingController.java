package Controlador;

import Modelo.Plane;
import Utilidad.Estado;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LandingController {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition landQueue = lock.newCondition();
    private final Condition takeOffQueue = lock.newCondition();

    private final ArrayList<Lock> landSites = new ArrayList<>();
    private final ArrayList<Plane> planes = new ArrayList<>();

    public LandingController(int landSiteNumber, int planeNumber) {
        Random rand = new Random();

        for (int i = 0; i < landSiteNumber; i++)
            landSites.add(new ReentrantLock());

        for (int i = 0; i < planeNumber; i++)
            planes.add(new Plane(rand.nextFloat(1000, 200000)));

        for (Plane plane : planes)
            new Thread(plane).start();
    }



    public Plane sortByPriority() {
        lock.lock();
        try {
            Plane priorityPlane = null;

            for (Plane plane : planes) {

                Estado s = plane.getState();
                if (s == Estado.ATERRIZANDO || s == Estado.DESPEGANDO){
                    continue;
                }

                if (priorityPlane == null) {
                    priorityPlane = plane;
                    continue;
                }

                float f = plane.getFuel();
                float pf = priorityPlane.getFuel();

                if (f < pf) {
                    priorityPlane = plane;
                } else if (f == pf) {
                    if (plane.getState() == Estado.EN_EL_AIRE && priorityPlane.getState() == Estado.EN_TERMINAL){
                        priorityPlane = plane;
                    } else if (plane.getState() == priorityPlane.getState() && plane.getId() < priorityPlane.getId()){
                        priorityPlane = plane;
                    }

                }
            }

            return priorityPlane;
        } finally {
            lock.unlock();
        }
    }



    public int planeRequest(Plane plane) throws InterruptedException {
        int landSiteId = -1;
        Estado deseado = plane.getState();

        lock.lock();
        try {
            while (true) {


                landSiteId = getLandSite();
                if (landSiteId != -1) {
                    return landSiteId;
                }


                Plane priority = sortByPriority();

                if (!plane.equals(priority)) {
                    plane.setWaiting(true);

                    if (deseado == Estado.EN_TERMINAL)
                        takeOffQueue.await();
                    else
                        landQueue.await();

                    continue;
                }


                plane.setWaiting(true);

                if (deseado == Estado.EN_TERMINAL){
                    takeOffQueue.await();
                } else{
                    landQueue.await();
                }

            }

        } finally {
            lock.unlock();
        }
    }
    public void releaseLandSite(int id) {
        landSites.get(id).unlock();
    }

    public void signalAll() {
        lock.lock();
        try {
            takeOffQueue.signalAll();
            landQueue.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private int getLandSite() {
        for (int i = 0; i < landSites.size(); i++) {
            Lock l = landSites.get(i);
            if (l.tryLock()) {
                return i;
            }
        }
        return -1;
    }




    public String getStates() {
        String out = "";
        for (Plane p : planes) {
            out += "ID " + p.getId() + " ESTADO: " + p.getState();

            if (p.isWaiting()) {
                out += " (ESPERANDO)";
            } else {
                out += "";
            }

            out += " FUEL: " + p.getFuel() + "\n";
        }
        return out;
    }



    public int getLandSites() {
        int free = 0;
        for (Lock l : landSites) {
            if (l.tryLock()) {
                free++;
                l.unlock();
            }
        }
        return free;
    }
}
