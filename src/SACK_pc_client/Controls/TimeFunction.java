package SACK_pc_client.Controls;

public class TimeFunction {


    private float scale;
    private float from;
    private float to;
    private float time;

    private boolean done=true;

    private long startTime;

    public TimeFunction(float time, float from, float to) {
        this.from=from;
        this.to=to;
        this.time=time;

        scale=to-from;
    }

    public void reconstruct(float time, float from, float to) {
        this.from=from;
        this.to=to;
        this.time=time;

        scale=to-from;
    }

    public void launch() {
        startTime=System.nanoTime();
        done=false;
    }

    private float getTimeFromStart() {
        return (System.nanoTime()-startTime)/1000000000f;
    }

    public float get4SpeedDownValue() {
        float currentTime=getTimeFromStart()/time-1;

        currentTime*=currentTime;

        return to-scale*currentTime*currentTime;
    }

    public float get4SpeedUpValue() {
        float currentTime=getTimeFromStart()/time;

        currentTime*=currentTime;

        return scale*currentTime*currentTime+from;
    }

    public float get2SpeedDownValue() {
        float currentTime=getTimeFromStart()/time-1;

        return to-scale*currentTime*currentTime;
    }

    public float get2SpeedUpValue() {
        float currentTime=getTimeFromStart()/time;

        return scale*currentTime*currentTime+from;
    }

    public float getLinearValue() {
        float currentTime=getTimeFromStart()/time;

        return from+(to-from)*currentTime;

    }

    public boolean isDone() {
        if (done) return true;
        done=getTimeFromStart()>=time;
        return done;
    }


}
