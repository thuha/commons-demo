package exo.portal.service.test;
 
import junit.framework.TestCase;
import org.exoplatform.services.log.*;
import org.exoplatform.container.*;
import exo.portal.service.*;
 
 
public class ServiceTestCase extends TestCase {
    private Newcomer newComer;
    private ExoLogger exoLogger;
 
    public void setUp() throws Exception {
         //StandaloneContainer myContainer = StandaloneContainer.getInstance();
    	PortalContainer myContainer = PortalContainer.getInstance();
         //ExoContainer myContainer = ExoContainerContext.getCurrentContainer();
         this.newComer = (Newcomer) myContainer.getComponentInstance(Newcomer.class);
         this.exoLogger = (ExoLogger) myContainer.getComponentInstance(ExoLogger.class);
    }
 
    public void testService(){
         this.newComer.test();
    }
}