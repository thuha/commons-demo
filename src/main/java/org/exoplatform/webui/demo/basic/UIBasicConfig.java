package org.exoplatform.webui.demo.basic;
 
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
 
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormStringInput;
 
@ComponentConfig(
    lifecycle = UIFormLifecycle.class,
    template = "system:/groovy/webui/form/UIForm.gtmpl",
    events = {
        @EventConfig(listeners = UIBasicConfig.SaveActionListener.class),
        @EventConfig(listeners = UIBasicConfig.CancelActionListener.class)
    }
)


public class UIBasicConfig extends UIForm {
 
    public static final String TEXT_STRING_INPUT = "UIBasicPortletTextStringInput";
    
    public UIBasicConfig() {
        PortletRequestContext portletRequestContext = WebuiRequestContext.getCurrentInstance();
        PortletPreferences preferences = portletRequestContext.getRequest().getPreferences();
        
        String text = preferences.getValue(UIBasicPortlet.TEXT_PREFERENCE, null);                
        addChild(new UIFormStringInput(TEXT_STRING_INPUT, text));                       
    }
 
    public static class SaveActionListener extends EventListener<UIBasicConfig> {
        public void execute(Event<UIBasicConfig> event) throws Exception {
            UIBasicConfig basicConfig = event.getSource();
            UIFormStringInput textStringInput = basicConfig.getUIStringInput(TEXT_STRING_INPUT);
            
            PortletRequestContext portletRequestContext = WebuiRequestContext.getCurrentInstance();
            PortletPreferences preferences = portletRequestContext.getRequest().getPreferences();
            preferences.setValue(UIBasicPortlet.TEXT_PREFERENCE, textStringInput.getValue());
            preferences.store();
            
            PortletRequestContext context = (PortletRequestContext) event.getRequestContext();
            context.setApplicationMode(PortletMode.VIEW);            
        }
    }
 
    public static class CancelActionListener extends EventListener<UIBasicConfig> {
        public void execute(Event<UIBasicConfig> event) throws Exception {
            PortletRequestContext context = (PortletRequestContext) event.getRequestContext();
            context.setApplicationMode(PortletMode.VIEW);
        }
    }
}
