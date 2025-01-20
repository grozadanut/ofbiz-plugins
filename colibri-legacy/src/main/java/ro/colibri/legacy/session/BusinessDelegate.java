package ro.colibri.legacy.session;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.transaction.GenericTransactionException;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import ro.colibri.beans.VanzariBean;
import ro.colibri.beans.VanzariBeanRemote;
import ro.colibri.entities.comercial.Product;
import ro.colibri.entities.comercial.mappings.ProductGestiuneMapping;

public class BusinessDelegate {
    private static final String MODULE = BusinessDelegate.class.getName();

    public static Map<String, Object> syncAllProducts(final DispatchContext ctx,
            final Map<String, ? extends Object> context) throws GenericTransactionException {
        final Delegator delegator = ctx.getDelegator();
        final LocalDispatcher dispatcher = ctx.getDispatcher();
        final Security security = ctx.getSecurity();
        final Locale locale = (Locale) context.get("locale");
        final Map<String, Object> successResult = ServiceUtil.returnSuccess();
        final List<GenericValue> toBeSynced = new LinkedList<>();
        final VanzariBeanRemote bean = ServiceLocator.getBusinessService(VanzariBean.class, VanzariBeanRemote.class);

        for (final Product legacyProd : bean.allProducts()) {
            if (legacyProd.getCategorie().equalsIgnoreCase("OBIECTE DE INVENTAR")
                    || legacyProd.getCategorie().equalsIgnoreCase("MIJLOACE FIXE")
                    || legacyProd.getCategorie().equalsIgnoreCase("DISCOUNT")
                    || legacyProd.getCategorie().equalsIgnoreCase("NOTIFICARE")) {
                continue;
            }

            final GenericValue prod = delegator.makeValue("Product");
            final String legacyId = legacyProd.getId() + "";
            prod.set("productId", legacyId);
            prod.set("productTypeId", mapProductType(legacyProd));
            prod.set("productName", legacyProd.getName());
            prod.set("internalName", legacyProd.getName());
            prod.set("isVirtual", "N");
            prod.set("isVariant", "N");
            prod.set("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
            prod.set("quantityUomId", mapUom(legacyProd.getUom()));

            final GenericValue sku = delegator.makeValue("GoodIdentification");
            sku.set("goodIdentificationTypeId", "SKU");
            sku.set("productId", legacyId);
            sku.set("idValue", legacyProd.getBarcode());

            toBeSynced.add(prod);
            toBeSynced.add(sku);
        }

        try {
            delegator.storeAll(toBeSynced);
        } catch (final GenericEntityException e) {
            Debug.logError(e, "An error occurred saving the data", MODULE);
            return ServiceUtil.returnError(Messages.nls(Messages.PRODUCT_SYNC_ERROR, locale) + e.getMessage() + ").");
        }

        return successResult;
    }

    public static Map<String, Object> syncStock(final DispatchContext ctx,
            final Map<String, ? extends Object> context) throws GenericTransactionException {
        final Delegator delegator = ctx.getDelegator();
        final Locale locale = (Locale) context.get("locale");
        final Map<String, Object> successResult = ServiceUtil.returnSuccess();
        final List<GenericValue> toBeSynced = new LinkedList<>();
        final VanzariBeanRemote bean = ServiceLocator.getBusinessService(VanzariBean.class, VanzariBeanRemote.class);

        for (final Product legacyProd : bean.allProducts()) {
            if (legacyProd.getCategorie().equalsIgnoreCase("OBIECTE DE INVENTAR")
                    || legacyProd.getCategorie().equalsIgnoreCase("MIJLOACE FIXE")
                    || legacyProd.getCategorie().equalsIgnoreCase("DISCOUNT")
                    || legacyProd.getCategorie().equalsIgnoreCase("NOTIFICARE")) {
                continue;
            }

            final String legacyId = legacyProd.getId() + "";

            final BigDecimal stcL1 = legacyProd.getStocuri().stream()
                    .filter(stoc -> stoc.getGestiune().getImportName().equals("L1")).findFirst()
                    .map(ProductGestiuneMapping::getStoc).orElse(BigDecimal.ZERO);
            final GenericValue invL1 = delegator.makeValue("InventoryItem");
            invL1.set("facilityId", "L1");
            invL1.set("inventoryItemId", legacyId + "L1");
            invL1.set("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
            invL1.set("productId", legacyId);
            invL1.set("ownerPartyId", "LINIC");
            invL1.set("currencyUomId", "RON");
            final GenericValue invL1Det = delegator.makeValue("InventoryItemDetail");
            invL1Det.set("inventoryItemId", legacyId + "L1");
            invL1Det.set("inventoryItemDetailSeqId", "001");
            invL1Det.set("availableToPromiseDiff", stcL1);
            invL1Det.set("quantityOnHandDiff", stcL1);
            invL1Det.set("accountingQuantityDiff", stcL1);

            final BigDecimal stcL2 = legacyProd.getStocuri().stream()
                    .filter(stoc -> stoc.getGestiune().getImportName().equals("L2")).findFirst()
                    .map(ProductGestiuneMapping::getStoc).orElse(BigDecimal.ZERO);
            final GenericValue invL2 = delegator.makeValue("InventoryItem");
            invL2.set("facilityId", "L2");
            invL2.set("inventoryItemId", legacyId + "L2");
            invL2.set("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
            invL2.set("productId", legacyId);
            invL2.set("ownerPartyId", "LINIC");
            invL2.set("currencyUomId", "RON");
            final GenericValue invL2Det = delegator.makeValue("InventoryItemDetail");
            invL2Det.set("inventoryItemId", legacyId + "L2");
            invL2Det.set("inventoryItemDetailSeqId", "001");
            invL2Det.set("availableToPromiseDiff", stcL2);
            invL2Det.set("quantityOnHandDiff", stcL2);
            invL2Det.set("accountingQuantityDiff", stcL2);

            toBeSynced.add(invL1);
            toBeSynced.add(invL1Det);
            toBeSynced.add(invL2);
            toBeSynced.add(invL2Det);
        }

        try {
            delegator.storeAll(toBeSynced);
        } catch (final GenericEntityException e) {
            Debug.logError(e, "An error occurred saving the data", MODULE);
            return ServiceUtil.returnError(Messages.nls(Messages.PRODUCT_SYNC_ERROR, locale) + e.getMessage() + ").");
        }

        return successResult;
    }

    private static String mapProductType(final Product legacyProd) {
        return switch (legacyProd.getCategorie()) {
        case "MARFA" -> "GOOD";
        case "AMBALAJE" -> "PACKING";
        case "MATERIE PRIMA" -> "RAW_MATERIAL";
        case "ALTE MATERIALE" -> "RAW_MATERIAL";
        case "PRODUS FINIT" -> "FINISHED_GOOD";
        case "PRESTARI SERVICII" -> "SERVICE";
        default -> throw new IllegalArgumentException("Unexpected value: " + legacyProd);
        };
    }

    private static String mapUom(final String uom) {
        return switch (uom) {
        case "BAX" -> "OTH_box";
        case "BUC" -> "OTH_ea";
        case "buc" -> "OTH_ea";
        case "CUT" -> "OTH_box";
        case "KG" -> "WT_kg";
        case "KM" -> "LEN_km";
        case "L" -> "VLIQ_L";
        case "LEI" -> "OTH_ea";
        case "M" -> "LEN_m";
        case "M2" -> "AREA_m2";
        case "MC" -> "VDRY_m3";
        case "ML" -> "LEN_m";
        case "MP" -> "AREA_m2";
        case "mp" -> "AREA_m2";
        case "ORE" -> "TF_hr";
        case "PAC" -> "OTH_pk";
        case "PAL" -> "OTH_ea";
        case "PER" -> "OTH_ea";
        case "PLACA" -> "OTH_ea";
        case "PRET" -> "OTH_ea";
        case "RAND" -> "OTH_ea";
        case "SAC" -> "OTH_ea";
        case "SET" -> "OTH_pk";
        case "SUL" -> "OTH_pk";
        case "T" -> "WT_mt";
        case "TO" -> "WT_mt";
        default -> throw new IllegalArgumentException("Unexpected value: " + uom);
        };
    }
}
