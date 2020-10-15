package cwcdh.pppp.bean;

import cwcdh.pppp.entity.EvaluationItem;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.entity.EvaluationGroup;
import cwcdh.pppp.entity.EvaluationSchema;
import cwcdh.pppp.facade.EvaluationItemFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@Named("evaluationItemController")
@SessionScoped
public class EvaluationItemController implements Serializable {

    @EJB
    private cwcdh.pppp.facade.EvaluationItemFacade ejbFacade;
    @Inject
    WebUserController webUserController;
    private List<EvaluationItem> items = null;
    private EvaluationItem selected;

    EvaluationSchema schema;
    EvaluationGroup group;
    List<EvaluationItem> groupItems = null;
    List<EvaluationGroup> groups;

    public EvaluationItemController() {
    }

    public void fillGroups() {
        if (schema == null) {
            groups = new ArrayList<>();
            return;
        }
        String j = "select g from EvaluationGroup g "
                + " where g.retired<>:ret "
                + " and g.evaluationSchema=:s "
                + " order by g.orderNo";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("s", schema);
        groups = getFacade().findByJpql(j, m);
        group = null;
    }

    public void fillGroupsItems() {
        if (group == null) {
            groupItems = new ArrayList<>();
            return;
        }
        String j = "select g from EvaluationItem g "
                + " where g.retired<>:ret "
                + " and g.evaluationGroup=:s "
                + " order by g.orderNo";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("s", group);
        groupItems = getFacade().findByJpql(j, m);
    }

    public EvaluationItem getSelected() {
        return selected;
    }

    public void setSelected(EvaluationItem selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EvaluationItemFacade getFacade() {
        return ejbFacade;
    }

    public EvaluationItem prepareCreate() {
        selected = new EvaluationItem();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleP4ppp1").getString("EvaluationItemCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void retire() {
        retire(selected);
        fillGroupsItems();
        items = null;
    }

    public void retire(EvaluationItem ei) {
        if (ei == null) {
            return;
        }
        if (ei.getId() == null) {
            return;
        }
        ei.setRetired(true);
        ei.setRetiredAt(new Date());
        ei.setRetiredBy(webUserController.getLoggedUser());
        getFacade().edit(ei);

    }

    public void save(){
        save(selected);
        fillGroupsItems();
        items = null;
    }
    
    public void save(EvaluationItem ei) {
        if (ei == null) {
            return;
        }
        if (ei.getId() == null) {
            ei.setCreatedAt(new Date());
            ei.setRetiredBy(webUserController.getLoggedUser());
            getFacade().create(ei);
        } else {
            ei.setLastEditeAt(new Date());
            ei.setLastEditBy(webUserController.getLoggedUser());
            getFacade().edit(ei);
        }

    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleP4ppp1").getString("EvaluationItemUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleP4ppp1").getString("EvaluationItemDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<EvaluationItem> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleP4ppp1").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleP4ppp1").getString("PersistenceErrorOccured"));
            }
        }
    }

    public EvaluationItem getEvaluationItem(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<EvaluationItem> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<EvaluationItem> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public EvaluationSchema getSchema() {
        return schema;
    }

    public void setSchema(EvaluationSchema schema) {
        this.schema = schema;
    }

    public EvaluationGroup getGroup() {
        return group;
    }

    public void setGroup(EvaluationGroup group) {
        this.group = group;
    }

    public List<EvaluationItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(List<EvaluationItem> groupItems) {
        this.groupItems = groupItems;
    }

    public List<EvaluationGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<EvaluationGroup> groups) {
        this.groups = groups;
    }

    @FacesConverter(forClass = EvaluationItem.class)
    public static class EvaluationItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EvaluationItemController controller = (EvaluationItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "evaluationItemController");
            return controller.getEvaluationItem(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof EvaluationItem) {
                EvaluationItem o = (EvaluationItem) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), EvaluationItem.class.getName()});
                return null;
            }
        }

    }

}
