package Modelo;

import Utilidad.Estado;

import java.util.Random;

import static Vista.Main.landingController;

public class Plane implements Runnable {

    private int id;
    private static int publicId = 1;
    private Estado state;
    private float fuel;
    private final float maxFuel = 200000;
    Random rand = new Random();

    public Estado getState() {
        return state;
    }

    public void setState(Estado state) {
        this.state = state;
    }
    public int getId() {
        return id;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public Plane(float fuel) {
        id=publicId;
        publicId++;
        Random rand = new Random();
        if(rand.nextBoolean()){
            state = Estado.EN_EL_AIRE;
        }else{
            state = Estado.EN_TERMINAL;
        }
        if(fuel>maxFuel){
            this.fuel = maxFuel;
        }else{
            this.fuel = fuel;
        }

    }
    @Override
    public void run() {
        Random rand = new Random();
        while(true){
            try {

                if (state==Estado.EN_EL_AIRE){
                    int timeInAir = rand.nextInt(5000,10000);
                    landingController.planeRequest(Plane.this);
                    Thread.sleep(timeInAir);
                }

                if (state==Estado.EN_TERMINAL){
                    landingController.planeRequest(Plane.this);
                    fuel=maxFuel;
                    Thread.sleep(rand.nextInt(5000,10000));
                }




            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void takeoff() throws InterruptedException {
        state =Estado.DESPEGANDO;
        Thread.sleep(rand.nextInt(1000,3000));
        state = Estado.EN_EL_AIRE;
    }
    public void land() throws InterruptedException {
        state =Estado.ATERRIZANDO;
        Thread.sleep(rand.nextInt(1000,3000));
        state = Estado.EN_TERMINAL;
    }
}
