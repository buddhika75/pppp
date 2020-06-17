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
    private cwcdh.pppp.facade.EvaluationSchemaFacade ejbFacade;
    @EJB
    private EvaluationItemFacade evaluationItemFacade;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Controllers">
    @Inject
    private EvaluationGroupController evaluationGroupController;
    @Inject
    private DesignComponentFormItemController designComponentFormItemController;
    @Inject
    private WebUserController webUserController;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Class Variables">
    private List<EvaluationSchema> items = null;
    private List<EvaluationSchema> insItems = null;
    private List<EvaluationItem> exportItems = null;
    private EvaluationSchema selected;
    private EvaluationSchema referanceSet;
    private Institution institution;
    private String backString;

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public EvaluationSchemaController() {
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Navigation Functions">
    public String backToManageFormSets() {
        return backString;
    }
    
    public String back(){
        backString = "";
        return backString;
    }
    
    public String toManageInstitutionFormssets(){
        String j = "Select s from EvaluationSchema s "
                + " where s.retired=:ret "
                + " and s.institution is not null"
                + " order by s.name";
        Map m = new HashMap();
        m.put("ret", false);
        backString = "/systemAdmin/index";
        items = getFacade().findByJpql(j, m);
        return "/evaluationSchema/List";
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Main Functions">
    
    
    
    public String toExport(){
        String j = "Select i from DesignComponentFormItem i where "
                + " i.retired=:r "
                + " and i.parentComponent.parentComponent=:p "
                + " order by i.parentComponent.name, i.parentComponent.parentComponent.name, i.orderNo";
        Map m = new HashMap();
        m.put("r", false);
        m.put("p", selected);
        exportItems = getEvaluationItemFacade().findByJpql(j, m);
        return "/evaluationSchema/export";
    }
    
    public void retire(){
        retire(selected);
    }
    
    public void retire(EvaluationSchema set){
        if(set==null){
            JsfUtil.addErrorMessage("Nothing is selected");
            return;
        }
        set.setRetired(true);
        set.setRetiredAt(new Date());
        set.setRetiredBy(webUserController.getLoggedUser());
        getFacade().edit(set);
    }
    
    public void importFormSet() {
        if (referanceSet == null) {
            JsfUtil.addErrorMessage("Formset to Import is NOT selected");
            return;
        }
        if (institution == null) {
            JsfUtil.addErrorMessage("Instituion NOT selected");
            return;
        }

        EvaluationSchema ns = (EvaluationSchema) SerializationUtils.clone(referanceSet);
        ns.setId(null);
        ns.setCreatedAt(new Date());
        ns.setCreatedBy(webUserController.getLoggedUser());
        ns.setLastEditBy(null);
        ns.setLastEditeAt(null);
        ns.setReferenceComponent(referanceSet);
        ns.setInstitution(institution);
        getFacade().create(ns);

        for (EvaluationGroup f : evaluationGroupController.fillFormsofTheSelectedSet(referanceSet)) {
            EvaluationGroup nf = (EvaluationGroup) SerializationUtils.clone(f);
            nf.setId(null);
            nf.setCreatedAt(new Date());
            nf.setCreatedBy(webUserController.getLoggedUser());
            nf.setLastEditBy(null);
            nf.setLastEditeAt(null);
            nf.setReferenceComponent(f);
            nf.setParentComponent(ns);
            nf.setInstitution(institution);
            evaluationGroupController.save(nf);

            for (EvaluationItem i : designComponentFormItemController.fillItemsOfTheForm(f)) {

                EvaluationItem ni = (EvaluationItem) SerializationUtils.clone(i);
                ni.setId(null);
                ni.setCreatedAt(new Date());
                ni.setCreatedBy(webUserController.getLoggedUser());
                ni.setLastEditBy(null);
                ni.setLastEditeAt(null);
                ni.setReferenceComponent(i);
                ni.setParentComponent(nf);
                ni.setInstitution(institution);
                designComponentFormItemController.saveItem(ni);

            }
        }
        insItems = null;
        fillInsItems();
        referanceSet = null;
        institution = null;
        JsfUtil.addSuccessMessage("Formset Successfully Imported.");

    }

    public void setBackStringToSysAdmin() {
        backString = "/evaluationSchema/List";
    }

    public void setBackStringToInsAdmin() {
        backString = "/evaluationSchema/List_Ins";
    }

    public String toAddFormsForTheSelectedSet() {
        evaluationGroupController.setEvaluationSchema(selected);
        evaluationGroupController.fillFormsofTheSelectedSet();
        evaluationGroupController.getAddingForm();
        return "/evaluationSchema/manage_forms";
    }

    public List<EvaluationSchema> fillInsItems() {
        return fillInsItems(webUserController.getLoggableInstitutions());
    }

    private List<EvaluationSchema> fillMasterSets() {
        String j = "Select s from EvaluationSchema s "
                + " where s.retired=false "
                + " and s.institution is null "
                + " order by s.name";
        List<EvaluationSchema> ss = getFacade().findByJpql(j);
        if (ss == null) {
            ss = new ArrayList<>();
        }
        return ss;
    }
    
    
    
    
    public List<EvaluationSchema> getClinicFormSets(Institution clinic) {
        String j = "Select s from EvaluationSchema s "
                + " where s.retired=false "
                + " and s.institution = :inss "
                + " order by s.name";
        Map m = new HashMap();
        m.put("inss", clinic);
        List<EvaluationSchema> ss = getFacade().findByJpql(j, m);
        if (ss == null) {
            ss = new ArrayList<>();
        }
        return ss;
    }


    public List<EvaluationSchema> fillInsItems(List<Institution> insLst) {
        String j = "Select s from EvaluationSchema s "
                + " where s.retired=false "
                + " and s.institution in :inss "
                + " order by s.name";
        Map m = new HashMap();
        m.put("inss", insLst);
        List<EvaluationSchema> ss = getFacade().findByJpql(j, m);
        if (ss == null) {
            ss = new ArrayList<>();
        }
        return ss;
    }

    public List<EvaluationSchema> completeFormSets(String qry) {
        String j = "Select s from EvaluationSchema s "
                + " where s.retired=false "
                + " and s.institution is null"
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
        if(selected==null){
            JsfUtil.addErrorMessage("Nothing to Delete");
            return ;
        }
        selected.setRetired(true);
        selected.setRetiredAt(new Date());
        selected.setRetiredBy(webUserController.getLoggedUser());
        getFacade().edit(selected);
        items = null;
        insItems=null;
        getItems();
        getInsItems();
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
   
    
    
    
    public EvaluationSchema getReferanceSet() {
        return referanceSet;
    }

    public void setReferanceSet(EvaluationSchema referanceSet) {
        this.referanceSet = referanceSet;
    }

    public EvaluationGroupController getEvaluationGroupController() {
        return evaluationGroupController;
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public cwcdh.pppp.facade.EvaluationSchemaFacade getEjbFacade() {
        return ejbFacade;
    }

    public List<EvaluationSchema> getInsItems() {
        if (insItems == null) {
            insItems = fillInsItems();
        }
        return insItems;
    }

    public void setInsItems(List<EvaluationSchema> insItems) {
        this.insItems = insItems;
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
        if (items == null) {
            items = fillMasterSets();
        }
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

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public DesignComponentFormItemController getDesignComponentFormItemController() {
        return designComponentFormItemController;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Converter">
    public String getBackString() {
        return backString;
    }

    
    
    public void setBackString(String backString) {
        this.backString = backString;
    }

    public List<EvaluationItem> getExportItems() {
        return exportItems;
    }

    public void setExportItems(List<EvaluationItem> exportItems) {
        this.exportItems = exportItems;
    }

    public void setEjbFacade(cwcdh.pppp.facade.EvaluationSchemaFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public void setEvaluationGroupController(EvaluationGroupController evaluationGroupController) {
        this.evaluationGroupController = evaluationGroupController;
    }

    public void setDesignComponentFormItemController(DesignComponentFormItemController designComponentFormItemController) {
        this.designComponentFormItemController = designComponentFormItemController;
    }

    public void setWebUserController(WebUserController webUserController) {
        this.webUserController = webUserController;
    }

    public EvaluationItemFacade getEvaluationItemFacade() {
        return evaluationItemFacade;
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
    // </editor-fold>

}
