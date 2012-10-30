    package org.exoplatform.webui.demo.basic;
     
    import javax.portlet.PortletPreferences;

    import org.exoplatform.webui.application.WebuiRequestContext;
    import org.exoplatform.webui.application.portlet.PortletRequestContext;
    import org.exoplatform.webui.config.annotation.ComponentConfig;
    import org.exoplatform.webui.core.UIComponent;
    import org.exoplatform.webui.core.lifecycle.Lifecycle;
     
    @ComponentConfig(
        lifecycle = Lifecycle.class,
        template = "app:/groovy/webui/BasicPortlet/UIBasicForm.gtmpl"
    )
    
    public class UIBasicForm extends UIComponent {
     
        private String text;
        
        public UIBasicForm() {              
            
        }
     
        public void init() {
            PortletRequestContext portletRequestContext = WebuiRequestContext.getCurrentInstance();
            PortletPreferences preferences = portletRequestContext.getRequest().getPreferences();
            text = preferences.getValue(UIBasicPortlet.TEXT_PREFERENCE, null);
        }
     
        public void processRender(WebuiRequestContext context) throws Exception {
            init();
            super.processRender(context);
        }
     
        public String getText() {
            return text;
        }
     
        public void setText(String text) {
            this.text = text;
        }
      
    }

