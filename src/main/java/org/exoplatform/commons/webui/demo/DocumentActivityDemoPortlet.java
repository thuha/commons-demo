/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.commons.webui.demo;

import javax.portlet.PortletMode;

import org.exoplatform.webui.application.WebuiApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;


/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Oct 22, 2012  
 */

@ComponentConfig(
                 lifecycle = UIApplicationLifecycle.class
                )

public class DocumentActivityDemoPortlet extends UIPortletApplication{
   
  public DocumentActivityDemoPortlet() throws Exception {
    super();
  }
  
  public void processRender(WebuiApplication app, WebuiRequestContext context) throws Exception {
    getChildren().clear();
    PortletRequestContext pContext = (PortletRequestContext) context ;
    PortletMode currentMode = pContext.getApplicationMode() ;
    
    if (getChild(UIDocumentDemo.class) == null) addChild(UIDocumentDemo.class, null, null);
        
    super.processRender(app, context) ;
}
  

}
