package ro.colibri.legacy.session;

import java.util.Locale;
import java.util.Map;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import ro.colibri.beans.VanzariBean;
import ro.colibri.beans.VanzariBeanRemote;

public class BusinessDelegate {
    public static Map<String, Object> allProducts(final DispatchContext ctx,
            final Map<String, ? extends Object> context) {
        final Delegator delegator = ctx.getDelegator();
        final LocalDispatcher dispatcher = ctx.getDispatcher();
        final Security security = ctx.getSecurity();
        final Locale locale = (Locale) context.get("locale");
        final Map<String, Object> successResult = ServiceUtil.returnSuccess();

        final VanzariBeanRemote bean = ServiceLocator.getBusinessService(VanzariBean.class, VanzariBeanRemote.class);
        successResult.put("products", bean.allProducts());
        return successResult;
    }
}
