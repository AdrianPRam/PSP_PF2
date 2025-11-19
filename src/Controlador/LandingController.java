package Controlador;

import Modelo.Plane;
import Utilidad.Estado;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LandingController {
    private ReentrantLock lock = new ReentrantLock();

    private Condition landQueue = lock.newCondition();
    private Condition takeOffQueue = lock.newCondition();

    private ArrayList<Lock> landSites = new ArrayList<>();
    private ArrayList<Plane> planes = new ArrayList<>();

    public LandingController(int landSiteNumber, int planeNumber) {
        Random rand = new Random();
        for (int i = 0; i < landSiteNumber; i++) {
            landSites.add(new ReentrantLock());
        }
        for (int i = 0; i < planeNumber; i++) {

            planes.add(new Plane(rand.nextFloat(1000,200000)));
        }
        for (Plane plane : planes) {
            Thread t = new Thread(plane);
            t.start();
        }
    }

    public Plane sortByPriority(){
        Plane highestPriorityPlane = null;
        for (Plane plane : planes) {
            if (highestPriorityPlane == null || plane.getFuel() < highestPriorityPlane.getFuel()) {
                highestPriorityPlane = plane;
            }
        }
        return highestPriorityPlane;
    }

    private int getLandSite() {
        for (int i = 0; i < landSites.size(); i++) {
            if (landSites.get(i).tryLock()) {
                return i;
            }
        }
        return -1;
    }


    public void planeRequest(Plane plane) throws InterruptedException {
        int landSiteId = -1;
        Estado state = plane.getState();
        lock.lock();
        try {
            while ((landSiteId = getLandSite()) == -1) {

                if (!plane.equals(sortByPriority())) {
                    plane.setState(Estado.EN_ESPERA);
                    if (state == Estado.EN_TERMINAL) {
                        takeOffQueue.await();
                    } else {
                        landQueue.await();
                    }
                }
            }
        } finally {
                lock.unlock();
        }

        try {
            if (state == Estado.EN_TERMINAL) {
                plane.takeoff();
            } else if (state == Estado.EN_EL_AIRE) {
                plane.land();
            }

        } finally {
            landSites.get(landSiteId).unlock();
            lock.lock();
            try {
                takeOffQueue.signalAll();
                landQueue.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    public String getStates() {
        String states="" ;
        for (Plane plane : planes) {
            states+="ID: "+plane.getId()+" ESTADO: "+plane.getState()+" COMBUSTIBLE: "+plane.getFuel()+"\n";
        }
        return states;
    }

    public int getLandSites() {
        int quant = 0;
        for (Lock landSite : landSites) {
            if (landSite.tryLock()) {
                try {
                    quant++;
                } finally {
                    landSite.unlock();
                }
            }
        }
        return quant;
    }
}