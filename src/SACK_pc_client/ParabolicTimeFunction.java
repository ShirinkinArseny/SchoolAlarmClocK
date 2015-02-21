package SACK_pc_client;

public class ParabolicTimeFunction {


    private float scale;
    private float from;
    private float to;
    private float time;
    private float timePow2;

    private long startTime;

    public ParabolicTimeFunction(float time, float from, float to) {
        this.from=from;
        this.to=to;
        this.time=time;

        timePow2=time*time;

        scale=to-from;
    }

    public void launch() {
        startTime=System.nanoTime();
    }

    private float getTimeFromStart() {
        return (System.nanoTime()-startTime)/1000000000f;
    }

    public float getSpeedDownValue() {
        float currentTime=getTimeFromStart()/time-1;

        return to-scale*currentTime*currentTime;
    }

    public float getSpeedUpValue() {
        float currentTime=getTimeFromStart()/time;

        return scale*currentTime*currentTime+from;

    }

    public boolean isDone() {
        return getTimeFromStart()>=time;
    }


}
