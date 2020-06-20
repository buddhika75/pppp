package cwcdh.pppp.bean;

import cwcdh.pppp.entity.SolutionEvaluationItem;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.facade.SolutionEvaluationItemFacade;

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

@Named("solutionEvaluationItemController")
@SessionScoped
public class SolutionEvaluationItemController implements Serializable {

    @EJB
    private cwcdh.pppp.facade.SolutionEvaluationItemFacade ejbFacade;
    private List<SolutionEvaluationItem> items = null;
    private SolutionEvaluationItem selected;

    public SolutionEvaluationItemController() {
    }

    public SolutionEvaluationItem getSelected() {
        return selected;
    }

    public void setSelected(SolutionEvaluationItem selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SolutionEvaluationItemFacade getFacade() {
        return ejbFacade;
    }

    public SolutionEvaluationItem prepareCreate() {
        selected = new SolutionEvaluationItem();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionEvaluationItemCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionEvaluationItemUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionEvaluationItemDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SolutionEvaluationItem> getItems() {
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

    public SolutionEvaluationItem getSolutionEvaluationItem(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<SolutionEvaluationItem> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SolutionEvaluationItem> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = SolutionEvaluationItem.class)
    public static class SolutionEvaluationItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SolutionEvaluationItemController controller = (SolutionEvaluationItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "solutionEvaluationItemController");
            return controller.getSolutionEvaluationItem(getKey(value));
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
            if (object instanceof SolutionEvaluationItem) {
                SolutionEvaluationItem o = (SolutionEvaluationItem) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SolutionEvaluationItem.class.getName()});
                return null;
            }
        }

    }

}
