package cwcdh.pppp.bean;

import cwcdh.pppp.entity.SolutionEvaluationScheme;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.facade.SolutionEvaluationSchemeFacade;

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

@Named("solutionEvaluationSchemeController")
@SessionScoped
public class SolutionEvaluationSchemeController implements Serializable {

    @EJB
    private cwcdh.pppp.facade.SolutionEvaluationSchemeFacade ejbFacade;
    private List<SolutionEvaluationScheme> items = null;
    private SolutionEvaluationScheme selected;

    public SolutionEvaluationSchemeController() {
    }

    public SolutionEvaluationScheme getSelected() {
        return selected;
    }

    public void setSelected(SolutionEvaluationScheme selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SolutionEvaluationSchemeFacade getFacade() {
        return ejbFacade;
    }

    public SolutionEvaluationScheme prepareCreate() {
        selected = new SolutionEvaluationScheme();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionEvaluationSchemeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionEvaluationSchemeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionEvaluationSchemeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SolutionEvaluationScheme> getItems() {
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

    public SolutionEvaluationScheme getSolutionEvaluationScheme(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<SolutionEvaluationScheme> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SolutionEvaluationScheme> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = SolutionEvaluationScheme.class)
    public static class SolutionEvaluationSchemeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SolutionEvaluationSchemeController controller = (SolutionEvaluationSchemeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "solutionEvaluationSchemeController");
            return controller.getSolutionEvaluationScheme(getKey(value));
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
            if (object instanceof SolutionEvaluationScheme) {
                SolutionEvaluationScheme o = (SolutionEvaluationScheme) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SolutionEvaluationScheme.class.getName()});
                return null;
            }
        }

    }

}
