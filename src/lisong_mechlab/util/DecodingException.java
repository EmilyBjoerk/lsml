package lisong_mechlab.util;


/**
 * An exception that is thrown from various decoding algorithms in the case that they fail to handle the given data.
 * 
 * @author Emily Björk
 */
public class DecodingException extends Exception{
   private static final long serialVersionUID = 8948178136779804692L;

   public DecodingException(String aString){
      super(aString);
   }

   public DecodingException(Throwable aThrowable){
      super(aThrowable);
   }
}
