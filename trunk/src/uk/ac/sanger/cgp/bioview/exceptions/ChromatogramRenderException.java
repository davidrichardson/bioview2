package uk.ac.sanger.cgp.bioview.exceptions;

/*
 * Copyright (c) 2006 Genome Research Ltd.
 * Author: Cancer Genome Project, cgpit@sanger.ac.uk
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the BSD License.
 *
 * Any redistribution or derivation in whole or in part including any
 * substantial portion of this code must include this copyright and permission
 * notice. 
 */

/**
 * Used to throw exceptions specific to the rendering of chromatogram components.
 * @author Original: kr2
 * @author $Author: kr2 $
 * @version $Revision: 1.2 $
 */
public class ChromatogramRenderException extends Exception {
	
	/**
   * Creates a new instance of {@link uk.ac.sanger.cgp.biodraw.exceptions.ChromatogramRenderExcpetion} without detail message.
   */
	public ChromatogramRenderException() {
		super();
	}
	
	
	/**
   * Constructs an instance of {@link uk.ac.sanger.cgp.biodraw.exceptions.ChromatogramRenderExcpetion} with specified detail message.
   * @param msg the detail message.
   */
	public ChromatogramRenderException(String msg) {
		super(msg);
	}
	
	/**
   * Constructs an instance of {@link uk.ac.sanger.cgp.biodraw.exceptions.ChromatogramRenderExcpetion} with specified causing exception.
   * @param t the throwable object which was the root cause of this exception
   */
	public ChromatogramRenderException(Throwable t) {
		super(t);
	}
	
	/**
   * Constructs an instance of {@link uk.ac.sanger.cgp.biodraw.exceptions.ChromatogramRenderExcpetion} with specified detail message and specified causing exception.
   * @param msg the detail message.
   * @param t the throwable object which was the root cause of this exception
   */
	public ChromatogramRenderException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
