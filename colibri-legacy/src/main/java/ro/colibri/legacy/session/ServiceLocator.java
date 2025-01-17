package ro.colibri.legacy.session;

import static ro.colibri.util.ServerConstants.EAR_VERSION;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import ro.colibri.beans.RemoteService;

public class ServiceLocator {
    private static final Map<String, RemoteService> SERVICES_CACHE;

    // The app name is the application name of the deployed EJBs. This is typically
    // the ear name
    // without the .ear suffix. However, the application name could be overridden in
    // the application.xml of the
    // EJB deployment on the server.
    // Since we haven't deployed the application as a .ear, the app name for us will
    // be an empty string
    private static final String APP_NAME = "colibri_stat_ear-" + EAR_VERSION;

    // This is the module name of the deployed EJBs on the server. This is typically
    // the jar name of the
    // EJB deployment, without the .jar suffix, but can be overridden via the
    // ejb-jar.xml
    // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar,
    // so the module name is
    // jboss-as-ejb-remote-app
    private static final String MODULE_NAME = "colibri_stat_ejb";

    public static final String INITIAL_CONTEXT_FACTORY_VALUE = "org.wildfly.naming.client.WildFlyInitialContextFactory";
    public static final String PROVIDER_URL_VALUE = System.getProperty(Context.PROVIDER_URL,
            "http-remoting://192.168.0.60:8080");

    static {
        SERVICES_CACHE = new HashMap<>();
    }

    public static String jndiNameStateless(final Class beanClass, final Class remoteInterface) {
        // The EJB name which by default is the simple class name of the bean
        // implementation class
        final String beanName = beanClass.getSimpleName();
        // the remote view fully qualified class name
        final String viewClassName = remoteInterface.getName();
        // ejb:<app-name>/<module-name>/<bean-name>!<fully-qualified-classname-of-the-remote-interface>
        return "ejb:" + APP_NAME + "/" + MODULE_NAME + "/" + beanName + "!" + viewClassName;
    }

    public static <C extends RemoteService> C getBusinessService(final Class beanClass, final Class remoteInterface) {
        final String jndiName = jndiNameStateless(beanClass, remoteInterface);
        final C businessService = (C) SERVICES_CACHE.get(jndiName);

        if (businessService != null) {
            return businessService;
        }

        try {
            final Properties jndiProps = new Properties();
            jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY_VALUE);
            jndiProps.put(Context.PROVIDER_URL, PROVIDER_URL_VALUE);
            jndiProps.put(Context.SECURITY_PRINCIPAL, System.getProperty("COLIBRI_LEGACY_USER", ""));
            jndiProps.put(Context.SECURITY_CREDENTIALS, System.getProperty("COLIBRI_LEGACY_PASS", ""));
            final InitialContext context = new InitialContext(jndiProps);
            final C businessServiceLoaded = (C) context.lookup(jndiName);
            SERVICES_CACHE.put(jndiName, businessServiceLoaded);
            context.close();
            return businessServiceLoaded;
        } catch (final NamingException e) {
            throw new UnsupportedOperationException("ServiceLocator - NamingException " + e.getMessage());
        }
    }

    public static void clearCache() {
        SERVICES_CACHE.clear();
    }
}
