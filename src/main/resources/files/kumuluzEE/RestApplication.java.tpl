package [# th:text="${java_package}"/];

import be.atbash.ee.jessie.test.demo.config.ConfigTestController;
import be.atbash.ee.jessie.test.demo.fault.FaultToleranceController;
import be.atbash.ee.jessie.test.demo.metric.MetricController;
import be.atbash.ee.jessie.test.demo.secure.ProtectedController;
import com.kumuluz.ee.jwt.auth.feature.JWTRolesAllowedDynamicFeature;
import com.kumuluz.ee.jwt.auth.filter.JWTAuthorizationFilter;
import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;


/**
 *
 */
@ApplicationPath("/data")
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({"protected"})
public class [# th:text="${artifact}"/]RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {

        Set<Class<?>> classes = new HashSet<>();

        // microprofile jwt auth filters
        classes.add(JWTAuthorizationFilter.class);
        classes.add(JWTRolesAllowedDynamicFeature.class);

        // resources
        classes.add(HelloController.class);
        classes.add(ConfigTestController.class);
        classes.add(FaultToleranceController.class);
        classes.add(MetricController.class);
        classes.add(ProtectedController.class);


        return classes;
    }

}
