package audioMatch;

/**
 *
 * @author Thilina 
 */

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;
import com.musicg.*;
import database.DBconnect;
import java.io.File;
import it.sauronsoftware.jave.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


public class Videotoaudio {
    
    static String oname=null;
    static String finame=null;
    static Connection con=null;
    static PreparedStatement pst = null;
    
    public static void ExtractAudio(String input) {
        
        System.out.println("Strat converting");
        finame=input;
        oname="C:\\Users\\Thilina\\Desktop\\ttt\\research\\file upload bird server\\converted\\converted.wav";
        File audio= new File(oname);
        AudioAttributes adio=new AudioAttributes();
        adio.setCodec("pcm_s16le");
        
        adio.setBitRate(new Integer(128000));
        adio.setChannels(new Integer(2));
//        adio.setSamplingRate(new Integer(44100));
        
        
        EncodingAttributes atr=new EncodingAttributes();
        atr.setFormat("wav");
        atr.setAudioAttributes(adio);
        
        Encoder encode= new Encoder();
        
        File Video = new File(finame);
        
        try {
            encode.encode(Video, audio, atr);
            System.out.println("Successfully Converted");
            
        } catch (Exception e) {
            System.err.println(e.toString());
        }
               
    }

    public static void inserttodb(){
         con=DBconnect.connect();
        double time=getDuration();
        String que="insert into advertisement (Name,Duration,Path) values (?,?,?)";
        try {
            pst=con.prepareStatement(que);
            pst.setString(1, finame);
            pst.setString(2, Double.toString(time));
            pst.setString(3, oname);
            pst.execute();
            
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static double getDuration(){
        double durationInSeconds=0;
                try {
                File ab= new File(oname);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(ab);
                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                durationInSeconds = (frames+0.0) / format.getFrameRate(); 
                
                
                
        } catch (Exception e) {
            System.out.println(e.toString());
        }
                return durationInSeconds;
    }

}
