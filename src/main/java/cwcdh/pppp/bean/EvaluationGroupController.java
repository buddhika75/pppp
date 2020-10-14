package cwcdh.pppp.bean;

import cwcdh.pppp.entity.EvaluationGroup;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.entity.EvaluationSchema;
import cwcdh.pppp.facade.EvaluationGroupFacade;

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

@Named("evaluationGroupController")
@SessionScoped
public class EvaluationGroupController implements Serializable {

    @EJB
    private cwcdh.pppp.facade.EvaluationGroupFacade ejbFacade;

    @Inject
    WebUserController webUserController;
    private List<EvaluationGroup> items = null;
    private EvaluationGroup selected;
    private EvaluationSchema evaluationSchema;
    private List<EvaluationGroup> schemaGroups = null;

    public void fillSchemaGroups() {
        if (evaluationSchema == null) {
            schemaGroups = new ArrayList<>();
            return;
        }
        String j = "select g from EvaluationGroup g "
                + " where g.retired<>:ret "
                + " and g.evaluationSchema=:s "
                + " order by g.orderNo";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("s", evaluationSchema);
        schemaGroups = getFacade().findByJpql(j, m);
    }

    public EvaluationGroupController() {
    }

    public EvaluationGroup getSelected() {
        return selected;
    }

    public void setSelected(EvaluationGroup selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EvaluationGroupFacade getFacade() {
        return ejbFacade;
    }

    public EvaluationGroup prepareCreate() {
        selected = new EvaluationGroup();
        initializeEmbeddableKey();
        return selected;
    }

    public void retire(EvaluationGroup eg) {
        if (eg == null) {
            return;
        }
        if (eg.getId() == null) {
            return;
        } else {
            eg.setRetired(true);
            eg.setRetiredAt(new Date());
            eg.setRetiredBy(webUserController.getLoggedUser());
            getFacade().edit(eg);
        }
    }

    public void retire() {
        retire(selected);
        fillSchemaGroups();
        items = null;
    }

    public void save() {
        save(selected);
        fillSchemaGroups();
        items = null;
    }

    public void save(EvaluationGroup eg) {
        if (eg == null) {
            return;
        }
        if (eg.getId() == null) {
            eg.setCreatedAt(new Date());
            eg.setCreatedBy(webUserController.getLoggedUser());
            getFacade().create(eg);
        } else {
            eg.setLastEditedAt(new Date());
            eg.setLastEditedBy(webUserController.getLoggedUser());
            getFacade().edit(eg);
        }
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle2").getString("EvaluationGroupCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle2").getString("EvaluationGroupUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle2").getString("EvaluationGroupDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<EvaluationGroup> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle2").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle2").getString("PersistenceErrorOccured"));
            }
        }
    }

    public EvaluationGroup getEvaluationGroup(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<EvaluationGroup> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<EvaluationGroup> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public EvaluationSchema getEvaluationSchema() {
        return evaluationSchema;
    }

    public void setEvaluationSchema(EvaluationSchema evaluationSchema) {
        this.evaluationSchema = evaluationSchema;
    }

    public List<EvaluationGroup> getSchemaGroups() {
        return schemaGroups;
    }

    public void setSchemaGroups(List<EvaluationGroup> schemaGroups) {
        this.schemaGroups = schemaGroups;
    }

    @FacesConverter(forClass = EvaluationGroup.class)
    public static class EvaluationGroupControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EvaluationGroupController controller = (EvaluationGroupController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "evaluationGroupController");
            return controller.getEvaluationGroup(getKey(value));
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
            if (object instanceof EvaluationGroup) {
                EvaluationGroup o = (EvaluationGroup) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), EvaluationGroup.class.getName()});
                return null;
            }
        }

    }

}
