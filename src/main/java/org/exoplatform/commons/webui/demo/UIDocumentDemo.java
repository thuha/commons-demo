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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.commons.UIDocumentSelector;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Oct 22, 2012  
 */

@ComponentConfig(
                 lifecycle = UIFormLifecycle.class,
                 template = "app:/groovy/webui/commons/UIDocumentDemo.gtmpl"                 
               )
public class UIDocumentDemo extends UIForm implements UIPopupComponent{
  
  private static final String UIDOCUMENTCOMP_ID = "COMMONS-DOCUMENT-UI";

  private static final Log LOG = ExoLogger.getLogger(UIDocumentDemo.class); 
  
  public UIDocumentDemo(){
    try {
      UIDocumentSelector documentSelector = addChild(UIDocumentSelector.class, null, UIDOCUMENTCOMP_ID);
      documentSelector.setShowUpload(true);
    } catch (Exception e) { //UIComponent.addChild() throws Exception()
      LOG.error("An exception happens when init UIDocumentDemo", e);
    }
  }

  @Override
  public void processRender(WebuiRequestContext context) throws Exception {   
    super.processRender(context);
  }
  
  @Override
  public void activate() throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deActivate() throws Exception {
    // TODO Auto-generated method stub
    
  }
  
  public class OpenDocumentActivityActionListener extends EventListener<UIDocumentDemo>{

    @Override
    public void execute(Event<UIDocumentDemo> event) throws Exception {
      
      
    }

  }
  
}
