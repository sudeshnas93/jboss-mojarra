/*
 * $Id: FormRenderer.java,v 1.3 2001/11/08 00:18:21 visvan Exp $
 *
 * Copyright 2000-2001 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

// FormRenderer.java

package com.sun.faces.renderkit.html_basic;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.OutputMethod;
import javax.faces.RenderContext;
import javax.faces.Renderer;
import javax.faces.WForm;
import javax.faces.WComponent;

import org.mozilla.util.Assert;
import org.mozilla.util.Debug;
import org.mozilla.util.Log;
import org.mozilla.util.ParameterCheck;

/**
 *
 *  <B>FormRenderer</B> is a class ...
 *
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: FormRenderer.java,v 1.3 2001/11/08 00:18:21 visvan Exp $
 * 
 * @see	Blah
 * @see	Bloo
 *
 */

public class FormRenderer extends Object implements Renderer
{
    //
    // Protected Constants
    //

    //
    // Class Variables
    //

    //
    // Instance Variables
    //

    // Attribute Instance Variables

    // Relationship Instance Variables

    //
    // Constructors and Initializers    
    //
    public FormRenderer()
    {
        super();
        // ParameterCheck.nonNull();
        this.init();
    }

    protected void init()
    {
        // super.init();
    }

    //
    // Class methods
    //

    //
    // General Methods
    //

    //
    // Methods From Renderer
    //

    /**
     * Returns an iterator containing the supported attribute names
     * for the specified component type.  This attribute list should
     * contain all attributes used by this renderer during the
     * rendering process.
     * @param componentType string representing the type of component
     * @return an iterator containing the Strings representing supported
     *          attribute names
     * @throws NullPointerException if componentType is null
     * @throws FacesException if the specified componentType is not
     *         supported by this renderer
     */
    public Iterator getSupportedAttributeNames(String componentType) {
        return null;
    }

    /**
     * This method is used to determine whether or not the specified
     * component type is supported by this renderer.
     * @throws NullPointerException if componentType is null
     * @return a boolean value indicating whether or not the specified
     *         component type can be rendered by this renderer
     */
    public boolean supportsType(String componentType) {
        return true;
    }

    /**
     * This method is used to determine whether or not the component
     * type of the specified component is supported by this renderer.
     * @throws NullPointerException if c is null
     * @return a boolean value indicating whether or not the specified
     *         component type can be rendered by this renderer
     */
    public boolean supportsType(WComponent c) {
        return true;
    }


    /**
     * Invoked to render the specified component using the specified 
     * render context.  An attribute value used during rendering
     * is obtained by first looking for a component-specific value
     * using c.getAttribute() and if not set directly on the component,
     * using the default value of that attribute defined by this renderer. 
     * @see WComponent#render
     * @see WComponent#getAttribute
     * @param rc the render context used to render the specified component
     * @param c the WComponent instance representing the component state
     *          being rendered
     * @throws IOException
     * @throws NullPointerException if rc or c is null
     */
    public void renderStart(RenderContext rc, WComponent c ) 
            throws IOException {
        // render the form
        try {
            WForm form = null;
            OutputMethod outputMethod = rc.getOutputMethod();
            if ( c instanceof WForm ) {
                form = (WForm) c;
            }
            StringBuffer out = new StringBuffer();
            out.append("<FORM ");

            String form_name = (String) form.getAttribute(rc, "name");
            if (form_name != null) {
                out.append("NAME=\"" + form_name + "\"");
            }
            out.append(">");
            outputMethod.writeText(out.toString());
            outputMethod.flush();

        } catch(IOException ioe) {
            System.err.println("Error rendering Form Tag: " + ioe);
        }
    }

    /**
     * Invoked to render the children of the specified component using
     * the specified render context.  If this renderer supports rendering
     * of a component type which returns <code>true</code> from the
     * getPerformsLayout() method, it must generate output required to
     * layout the children of the specified component, as well as drive
     * the render process of those children by invoking renderAll() on
     * each child.  If the specified component type returns <code>false</code>
     * from getPerformsLayout() then this method should do nothing.
     * @see WComponent#renderAll
     * @see WComponent#renderChildren
     * @param rc the render context used to render the specified component
     * @param c the WComponent instance representing the component state
     *          being rendered
     * @throws IOException
     * @throws NullPointerException if rc or c is null
     */
    public void renderChildren(RenderContext rc, 
            WComponent c) throws IOException {
        return;
    }

    /**
     * Invoked after all of the specified component's descendents have
     * been rendered. 
     * @see WComponent#postRender 
     * @param rc the render context used to render the specified component
     * @param c the WComponent instance representing the component state
     *          being rendered
     * @throws IOException
     * @throws NullPointerException if rc or c is null
     */
    public void renderEnd(RenderContext rc, WComponent c )
            throws IOException {

        // render the form
        try {
            OutputMethod outputMethod = rc.getOutputMethod();
            StringBuffer out = new StringBuffer();
            out.append("</FORM>");
            outputMethod.writeText(out.toString());
            outputMethod.flush();
        } catch(IOException ioe) {
            System.err.println("Error rendering Form Tag: " + ioe);
        }

        return;
    }

    
    // ----VERTIGO_TEST_START

    //
    // Test methods
    //

    public static void main(String [] args)
    {
        Assert.setEnabled(true);
        FormRenderer me = new FormRenderer();
        Log.setApplicationName("FormRenderer");
        Log.setApplicationVersion("0.0");
        Log.setApplicationVersionDate("$Id: FormRenderer.java,v 1.3 2001/11/08 00:18:21 visvan Exp $");
    
    }

    // ----VERTIGO_TEST_END

} // end of class FormRenderer
