package PC_part.J35DB_pc_part;

import javax.sound.sampled.*;

public class Recorder extends Thread {

    private TargetDataLine line;
    private AudioInputStream inputStream;

    private Recorder() {
        try {
            AudioFormat audioFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100.0F,       8,      2, 2,         44100.0F,       false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            this.line = (TargetDataLine) AudioSystem.getLine(info);
            this.line.open(audioFormat);
            this.inputStream = new AudioInputStream(this.line);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {
        this.line.start();
        start();
    }

    public void stopRecording() {
        this.line.stop();
        this.line.close();
    }

    private void processByte() {
        int sum=0;
        for (int i=0; i<8; i++) {
            sum+=data[i]*Math.pow(2, 7-i);
        }
        String msg=Lang.parseByte(sum);
        if (msg!=null)
            System.out.print(msg);
        //else System.err.println("ERROR: unknown bit sequence" + Arrays.toString(data)+" (normal if *duino connected just right now)");
    }

    private int pos=-1;
    private final int[] data=new int[]{-1, -1, -1, -1, -1, -1, -1, -1};
    private void processBit(boolean value) {
        //System.out.println(System.currentTimeMillis()+" : get bit : "+value+" ");

        System.arraycopy(data, 1, data, 0, 7);data[7]=value?1:0;
        //System.out.println("Bit process array=" + Arrays.toString(data));

        boolean syncMarker=data[3]==1&&
                data[4]==0&&data[5]==0&&
                data[6]==0&&data[7]==0;

        if (syncMarker) {
            pos=9;
            //System.out.println("PROCESS SYNC : " + Arrays.toString(data));
        } else {
            pos--;

            if (pos==0) {
                processByte();
                //System.out.println("PROCESS BYTE : " + Arrays.toString(data));
                pos=8;
            }

        }
    }

    public void run() {
        try {
            int length=4;
            byte[] data = new byte[length];
            long peak = -100000;
            while (true) {
                long sumLeft=0;
                long sumRight=0;
                inputStream.read(data, 0, length);
                for (int i=0; i<length; i++) {

                    switch (i%2) {
                        case 0:sumRight+= data[i]; break;
                        case 1:sumLeft+= data[i];     break;
                    }

                }
                sumLeft/=length/2;
                sumRight/=length/2;
                if (sumLeft>60 && Math.abs(peak -sumLeft)>120) {
                    processBit(sumRight > 60);
                    //long oldPeak=peak;
                        peak =sumLeft;
                    //System.out.println(System.currentTimeMillis()+" : get bit : "+(sumRight > 30)+" new peak: "+peak+" old: "+oldPeak);
                } else
                    if (sumLeft<-30 && Math.abs(peak -sumLeft)>120) {
                        peak =sumLeft;
                        //System.out.println(System.currentTimeMillis()+" : tick down : "+peak);

                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Recorder r = new Recorder();
            r.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}