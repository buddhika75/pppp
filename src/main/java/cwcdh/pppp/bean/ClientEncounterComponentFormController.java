package cwcdh.pppp.bean;

import cwcdh.pppp.entity.SolutionEvaluationGroup;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.facade.ClientEncounterComponentFormFacade;

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
import cwcdh.pppp.entity.SolutionEvaluationScheme;

@Named
@SessionScoped
public class ClientEncounterComponentFormController implements Serializable {

    @EJB
    private cwcdh.pppp.facade.ClientEncounterComponentFormFacade ejbFacade;

    @Inject
    private WebUserController webUserController;

    private List<SolutionEvaluationGroup> items = null;
    private SolutionEvaluationGroup selected;

    public void save() {
        save(selected);
    }

    public void save(SolutionEvaluationGroup f) {
        if (f == null) {
            return;
        }
        if (f.getId() == null) {
            f.setCreatedAt(new Date());
            f.setCreatedBy(webUserController.getLoggedUser());
            getFacade().create(f);
        } else {
            f.setLastEditBy(webUserController.getLoggedUser());
            f.setLastEditeAt(new Date());
            getFacade().edit(f);
        }
    }

    public ClientEncounterComponentFormController() {
    }

    
    public List<SolutionEvaluationGroup> findClientEncounterComponentFormOfAFormset(SolutionEvaluationScheme fs){
        String j = "select f from SiComponentForm f "
                + " where f.retired=false "
                + " and f.parentComponent=:p "
                + " order by f.orderNo";
        Map m = new HashMap();
        m.put("p", fs);
        List<SolutionEvaluationGroup> t= getFacade().findByJpql(j, m);
        if(t ==null){
           t = new ArrayList<>();
        }
        return t;
    }
    
    public SolutionEvaluationGroup getSelected() {
        return selected;
    }

    public void setSelected(SolutionEvaluationGroup selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ClientEncounterComponentFormFacade getFacade() {
        return ejbFacade;
    }

    public SolutionEvaluationGroup prepareCreate() {
        selected = new SolutionEvaluationGroup();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleClinical").getString("ClientEncounterComponentFormCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleClinical").getString("ClientEncounterComponentFormUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleClinical").getString("ClientEncounterComponentFormDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SolutionEvaluationGroup> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleClinical").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleClinical").getString("PersistenceErrorOccured"));
            }
        }
    }

    public SolutionEvaluationGroup getClientEncounterComponentForm(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<SolutionEvaluationGroup> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SolutionEvaluationGroup> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public cwcdh.pppp.facade.ClientEncounterComponentFormFacade getEjbFacade() {
        return ejbFacade;
    }

    @FacesConverter(forClass = SolutionEvaluationGroup.class)
    public static class ClientEncounterComponentFormControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ClientEncounterComponentFormController controller = (ClientEncounterComponentFormController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "clientEncounterComponentFormController");
            return controller.getClientEncounterComponentForm(getKey(value));
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
            if (object instanceof SolutionEvaluationGroup) {
                SolutionEvaluationGroup o = (SolutionEvaluationGroup) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SolutionEvaluationGroup.class.getName()});
                return null;
            }
        }

    }

}
