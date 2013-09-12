package lisong_mechlab.util;

/**
 * Utility helper to make certain operations on bytes behave like you'd expect to.
 * 
 * @author Emily Björk
 */
public class ByteUtil{

   /**
    * Performs a "Logical Shift Left" (lsl) operation on the byte.
    * 
    * @param b
    *           The byte to shift
    * @param shift
    *           The number of positions to shift.
    * @return The shifted byte
    */
   static public byte lsl(final int b, final int shift){
      return (byte)((b & 0xFF) << shift);
   }

   /**
    * Performs a "Logical Shift Right" (lsr) operation on the byte.
    * 
    * @param b
    *           The byte to shift
    * @param shift
    *           The number of positions to shift.
    * @return The shifted byte
    */
   static public byte lsr(final int b, final int shift){
      return (byte)((b & 0xFF) >>> shift);
   }
}
