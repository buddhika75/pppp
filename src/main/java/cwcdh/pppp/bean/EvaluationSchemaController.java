package cwcdh.pppp.bean;

import cwcdh.pppp.entity.EvaluationSchema;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.facade.EvaluationSchemaFacade;

import java.io.Serializable;
import java.util.List;
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

@Named("evaluationSchemaController")
@SessionScoped
public class EvaluationSchemaController implements Serializable {

    @EJB
    private cwcdh.pppp.facade.EvaluationSchemaFacade ejbFacade;
    private List<EvaluationSchema> items = null;
    private EvaluationSchema selected;

    public EvaluationSchemaController() {
    }

    public EvaluationSchema getSelected() {
        return selected;
    }

    public void setSelected(EvaluationSchema selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EvaluationSchemaFacade getFacade() {
        return ejbFacade;
    }

    public EvaluationSchema prepareCreate() {
        selected = new EvaluationSchema();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle2").getString("EvaluationSchemaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle2").getString("EvaluationSchemaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle2").getString("EvaluationSchemaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<EvaluationSchema> getItems() {
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

    public EvaluationSchema getEvaluationSchema(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<EvaluationSchema> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<EvaluationSchema> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = EvaluationSchema.class)
    public static class EvaluationSchemaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EvaluationSchemaController controller = (EvaluationSchemaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "evaluationSchemaController");
            return controller.getEvaluationSchema(getKey(value));
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
            if (object instanceof EvaluationSchema) {
                EvaluationSchema o = (EvaluationSchema) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), EvaluationSchema.class.getName()});
                return null;
            }
        }

    }

}
