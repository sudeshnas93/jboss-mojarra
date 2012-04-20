/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 * 
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 * 
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.

 */
package com.sun.faces.flow;

import com.sun.faces.config.WebConfiguration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.flow.Flow;
import javax.faces.flow.FlowHandler;
import javax.faces.flow.ViewNode;

public class FlowHandlerImpl extends FlowHandler {

    public FlowHandlerImpl() {
        WebConfiguration config = WebConfiguration.getInstance();
        flowFeatureIsEnabled = config.isHasFlows();
        flows = new ConcurrentHashMap<String, Flow>();
    }
    
    private boolean flowFeatureIsEnabled;
    private Map<String, Flow> flows;

    @Override
    public Map<Object, Object> getCurrentFlowScope() {
        return FlowCDIContext.getCurrentFlowScope();
    }
    
    @Override
    public Flow getFlow(String id) {
        Flow result = flows.get(id);
        
        return result;
    }

    @Override
    public Flow getFlowByNodeId(String id) {
        if (null == id || 0 == id.length()) {
            throw new IllegalStateException();
        }
        Flow result = null;
        for (Flow cur : flows.values()) {
            for (ViewNode curView : cur.getViews()) {
                if (id.equals(curView.getId())) {
                    result = cur;
                    break;
                }
            }
            if (null != result) {
                break;
            }
        }
        
        return result;
    }
    
    

    @Override
    public void addFlow(Flow toAdd) {
        if (null == toAdd || null == toAdd.getId() || 0 == toAdd.getId().length()) {
            throw new IllegalArgumentException();
        }
        flows.put(toAdd.getId(), toAdd);
    }

    @Override
    public boolean isActive(FacesContext context, String id) {
        boolean result = false;
        Deque<Flow> flowStack = getFlowStack(context);
        for (Flow cur : flowStack) {
            if (id.equals(cur.getId())) {
                result = true;
                break;
            }
        }
        
        return result;
    }
    
    
    
    
    @Override
    public Flow getCurrentFlow(FacesContext context) {
        if (!flowFeatureIsEnabled) {
            return null;
        }
        return getFlowStack(context).peek();
    }
            
    // We need a method that takes a view id of a view that is in a flow
    // and makes the system "enter" the flow.
    
    @Override
    @SuppressWarnings(value="")
    public void transition(FacesContext context, UIComponent src, UIComponent target) {
        if (!flowFeatureIsEnabled) {
            return;
        }
        
        String  sourceFlowId = getSourceFlowId(context, src),
                targetFlowId = getTargetFlowId(context, target);
        Flow sourceFlow = this.getFlowByNodeId(sourceFlowId),
             targetFlow = this.getFlowByNodeId(targetFlowId);
        if (    (null == sourceFlow && null != targetFlow) ||
                (null != sourceFlow && null == targetFlow) ||
                !sourceFlow.equals(targetFlow) ) {
            popFlow(context);
            if (null != targetFlow) {
                pushFlow(context, targetFlow);
            }
        }
        
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Helper Methods">
    
    private void pushFlow(FacesContext context, Flow toPush) {
        Deque<Flow> flowStack = getFlowStack(context);
        flowStack.push(toPush);
        FlowCDIContext.flowEntered();
    }
    
    private Flow peekFlow(FacesContext context) {
        Deque<Flow> flowStack = getFlowStack(context);
        return flowStack.peek();
    }
    
    private Flow popFlow(FacesContext context) {
        Deque<Flow> flowStack = getFlowStack(context);
        Flow currentFlow = peekFlow(context);
        if (null != currentFlow) {
            FlowCDIContext.flowExited();
        }
        return flowStack.pollFirst();
        
    }
    
    private Deque<Flow> getFlowStack(FacesContext context) {
        Deque<Flow> result = null;
        ExternalContext extContext = context.getExternalContext();
        String sessionKey = extContext.getClientWindow().getId() + "_flowStack";
        Map<String, Object> sessionMap = extContext.getSessionMap();
        result = (Deque<Flow>) sessionMap.get(sessionKey);
        if (null == result) {
            result = new ArrayDeque<Flow>();
            sessionMap.put(sessionKey, result);
        }
        
        return result;
    }
    
    private String getSourceFlowId(FacesContext context, UIComponent source) {
        String result = "";
        if (source instanceof javax.faces.component.UIViewRoot) {
            result = ((javax.faces.component.UIViewRoot)source).getViewId();
            int dot = result.indexOf(".");
            if (-1 != dot) {
                result = result.substring(0, dot);
            }
            
        }
        
        return result;
    }
    
    private String getTargetFlowId(FacesContext context, UIComponent target) {
        String result = "";
        if (target instanceof javax.faces.component.UIViewRoot) {
            result = ((javax.faces.component.UIViewRoot)target).getViewId();
            int dot = result.indexOf(".");
            if (-1 != dot) {
                result = result.substring(0, dot);
            }
        }
        
        return result;
    }
    
    // </editor-fold>
    
}
