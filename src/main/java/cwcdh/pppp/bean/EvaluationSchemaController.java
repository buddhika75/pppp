package cwcdh.pppp.bean;

// <editor-fold defaultstate="collapsed" desc="Imports">
import cwcdh.pppp.entity.EvaluationSchema;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.facade.EvaluationSchemaFacade;

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
import cwcdh.pppp.entity.EvaluationGroup;
import cwcdh.pppp.entity.EvaluationItem;
import cwcdh.pppp.entity.Institution;
import cwcdh.pppp.facade.EvaluationItemFacade;
import org.apache.commons.lang3.SerializationUtils;
// </editor-fold>

@Named
@SessionScoped
public class EvaluationSchemaController implements Serializable {

    // <editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private EvaluationSchemaFacade ejbFacade;
    @EJB
    private EvaluationItemFacade evaluationItemFacade;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Controllers">
    @Inject
    private EvaluationGroupController evaluationGroupController;
    @Inject
    private EvaluationItemController evaluationItemController;
    @Inject
    private WebUserController webUserController;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Class Variables">
    private List<EvaluationSchema> items = null;
    private EvaluationSchema selected;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public EvaluationSchemaController() {
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Navigation Functions">
    public String backToManageEvaluationSchemas() {
        return "/evaluationSchema/List";
    }

    public String toManageEvaluationSchemas() {
        items = fillEvaluationSchemas();
        return "/evaluationSchema/List";
    }

    public String toAddFormsForTheSelectedSet() {
        evaluationGroupController.setEvaluationSchema(selected);
        evaluationGroupController.fillFormsofTheSelectedSet();
        evaluationGroupController.getAddingForm();
        return "/evaluationSchema/manage_forms";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Main Functions">
    public void retire() {
        retire(selected);
    }

    public void retire(EvaluationSchema set) {
        if (set == null) {
            JsfUtil.addErrorMessage("Nothing is selected");
            return;
        }
        set.setRetired(true);
        set.setRetiredAt(new Date());
        set.setRetiredBy(webUserController.getLoggedUser());
        getFacade().edit(set);
    }

    private List<EvaluationSchema> fillEvaluationSchemas() {
        String j = "Select s from EvaluationSchema s "
                + " where s.retired=false "
                + " order by s.name";
        List<EvaluationSchema> ss = getFacade().findByJpql(j);
        if (ss == null) {
            ss = new ArrayList<>();
        }
        return ss;
    }

    public List<EvaluationSchema> completeEvaluations(String qry) {
        String j = "Select s from EvaluationSchema s "
                + " where s.retired=false "
                + " and lower(s.name) like :q "
                + " order by s.name";
        Map m = new HashMap();
        m.put("q", "%" + qry.trim().toLowerCase() + "%");
        List<EvaluationSchema> ss = getFacade().findByJpql(j, m);
        if (ss == null) {
            ss = new ArrayList<>();
        }
        return ss;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Default Functions">
    public EvaluationSchema prepareCreate() {
        selected = new EvaluationSchema();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleClinical").getString("EvaluationSchemaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleClinical").getString("EvaluationSchemaUpdated"));
    }

    public void destroy() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to Delete");
            return;
        }
        selected.setRetired(true);
        selected.setRetiredAt(new Date());
        selected.setRetiredBy(webUserController.getLoggedUser());
        getFacade().edit(selected);
        items = null;
 
        getItems();
 
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

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public EvaluationGroupController getEvaluationGroupController() {
        return evaluationGroupController;
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public EvaluationSchemaFacade getEjbFacade() {
        return ejbFacade;
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

    public List<EvaluationSchema> getItems() {
        return items;
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

    public EvaluationItemController getEvaluationItemController() {
        return evaluationItemController;
    }

    public EvaluationItemFacade getEvaluationItemFacade() {
        return evaluationItemFacade;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Converter">
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
    // </editor-fold>

}
