/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audioMatch;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;
/**
 *
 * @author Thilina
 */
public class fingerprint {
    
    public static float fingerprinting(String mclip,String sclip) {
        System.out.println("starting fingerprinting");
        float a=0;
        String main=mclip;
        String check=sclip;
        
        Wave wmain= new Wave(main);
        Wave wcheck= new Wave(check);
        
        
        
        FingerprintSimilarity sim=wmain.getFingerprintSimilarity(wcheck);
        float result = sim.getsetMostSimilarTimePosition();
        
        a =sim.getScore();
        
        System.out.println("end fingerprinting");
        return a;
        
    }
    
    public static int max(float[] array) {
      // Validates input
      if (array == null) {
          throw new IllegalArgumentException("The Array must not be null");
      } else if (array.length == 0) {
          throw new IllegalArgumentException("Array cannot be empty.");
      }

      // Finds and returns max
      float max = array[0];
      int maxIndex = 0;
      for (int j = 1; j < array.length; j++) {
          
          if (array[j] > max) {
              max = array[j];
                maxIndex = j ;
          }
      }

      return maxIndex;
  }
}
