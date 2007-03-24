package uk.ac.sanger.cgp.bioview.exceptions;

/**
 * Used for any exception arising from erroneous input data or files that has not been handled in a more appropriate manner.
 * @author Original: kr2
 * @author  $Author: kr2 $
 * @version $Revision: 1.2 $
 */
public class BiodrawRuntimeException extends RuntimeException {
	
	/**
   * Creates a new instance of {@link uk.ac.sanger.cgp.standalonecsa.exceptions.BiodrawRuntimeException} without detail message.
   */
	public BiodrawRuntimeException() {
		super();
	}
	
	
	/**
   * Constructs an instance of {@link uk.ac.sanger.cgp.standalonecsa.exceptions.BiodrawRuntimeException} with specified detail message.
   * 
   * @param msg the detail message.
   */
	public BiodrawRuntimeException(String msg) {
		super(msg);
	}
	
	/**
   * Constructs an instance of {@link uk.ac.sanger.cgp.standalonecsa.exceptions.BiodrawRuntimeException} with specified causing exception.
   * 
   * @param t The Throwable object which was the root cause of this exception
   */
	public BiodrawRuntimeException(Throwable t) {
		super(t);
	}
	
	/**
   * Constructs an instance of {@link uk.ac.sanger.cgp.standalonecsa.exceptions.BiodrawRuntimeException} with specified detail message and specified causing exception.
   * 
   * @param msg the detail message.
   * @param t The Throwable object which was the root cause of this exception
   */
	public BiodrawRuntimeException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
