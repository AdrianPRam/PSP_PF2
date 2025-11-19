package Modelo;

import Utilidad.Estado;
import java.util.Random;
import static Vista.Main.landingController;

public class Plane implements Runnable {

    private final int id;
    private static int publicId = 1;

    private Estado state;
    private final float fuel;

    private final Random rand = new Random();
    private boolean waiting = false;

    public boolean isWaiting() {
        return waiting;
    }
    public void setWaiting(boolean w) {
        waiting = w;
    }

    public Plane(float fuel) {
        this.id = publicId;
        publicId++;
        if(rand.nextBoolean()) {
            this.state=Estado.EN_EL_AIRE;
        }else{
            this.state=Estado.EN_TERMINAL;
        }
        this.fuel = fuel;
    }

    public int getId() {
        return id;
    }
    public Estado getState() {
        return state;
    }
    public float getFuel() {
        return fuel;
    }
    public void setState(Estado s) {
        state = s;
    }


    public void run() {
        Random r = new Random();

        while (true) {
            try {
                Thread.sleep(r.nextInt(5000, 10000));

                int pista = landingController.planeRequest(this);

                if (pista != -1) {


                    if (state == Estado.EN_EL_AIRE) {
                        land();
                        state = Estado.EN_TERMINAL;
                    } else {
                        takeoff();
                        state = Estado.EN_EL_AIRE;
                    }


                    landingController.releaseLandSite(pista);


                    landingController.signalAll();

                    waiting = false;
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }




    private void takeoff() throws InterruptedException {
        setState(Estado.DESPEGANDO);
        Thread.sleep(rand.nextInt(1200, 3000));
        setState(Estado.EN_EL_AIRE);

    }

    private void land() throws InterruptedException {
        setState(Estado.ATERRIZANDO);
        Thread.sleep(rand.nextInt(1200, 3000));
        setState(Estado.EN_TERMINAL);

    }
}
