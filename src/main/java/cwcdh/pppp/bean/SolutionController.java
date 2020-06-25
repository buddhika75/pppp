package cwcdh.pppp.bean;

import cwcdh.pppp.entity.Solution;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.entity.EvaluationGroup;
import cwcdh.pppp.entity.EvaluationItem;
import cwcdh.pppp.entity.EvaluationSchema;
import cwcdh.pppp.entity.SolutionEvaluationGroup;
import cwcdh.pppp.entity.SolutionEvaluationItem;
import cwcdh.pppp.entity.SolutionEvaluationSchema;
import cwcdh.pppp.facade.EvaluationGroupFacade;
import cwcdh.pppp.facade.EvaluationItemFacade;
import cwcdh.pppp.facade.EvaluationSchemaFacade;
import cwcdh.pppp.facade.SolutionEvaluationGroupFacade;
import cwcdh.pppp.facade.SolutionEvaluationItemFacade;
import cwcdh.pppp.facade.SolutionEvaluationSchemeFacade;
import cwcdh.pppp.facade.SolutionFacade;
import cwcdh.pppp.pojcs.PoEi;
import cwcdh.pppp.pojcs.Poe;
import cwcdh.pppp.pojcs.Poeg;

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

@Named("solutionController")
@SessionScoped
public class SolutionController implements Serializable {

    @EJB
    private SolutionFacade facade;
    @EJB
    private SolutionEvaluationSchemeFacade sesFacade;
    @EJB
    private SolutionEvaluationGroupFacade segFacade;
    @EJB
    private SolutionEvaluationItemFacade seiFacade;
    @EJB
    private EvaluationSchemaFacade esFacade;
    @EJB
    private EvaluationGroupFacade egFacade;
    @EJB
    private EvaluationItemFacade eiFacade;

    @Inject
    private WebUserController webUserController;

    private List<Solution> items = null;
    private Solution selected;
    private List<SolutionEvaluationSchema> solutionEvaluationSchemas;
    private SolutionEvaluationSchema solutionEvaluationSchema;
    private EvaluationSchema evaluationSchema;

    public SolutionController() {
    }

    public String toCreateNewSolution() {
        selected = new Solution();
        return "/solution/solution";
    }

    public String toEditSolution() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return "";
        }
        return "/solution/solution";
    }

    public String toSolutionEvaluations() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return "";
        }
        String j;
        Map m = new HashMap();
        j = "Select ses from SolutionEvaluationSchema ses "
                + " where ses.retired=:ret "
                + " and ses.solution=:sol "
                + " order by ses.orderNo";

        m.put("ret", false);
        m.put("sol", selected);
        if (false) {
            SolutionEvaluationSchema s = new SolutionEvaluationSchema();
            s.getSolution();
            s.isRetired();
        }
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/evaluations";
    }

    public String toEditSolutionEvaluation() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return "";
        }

        return "/solution/evaluation";
    }

    public String toNewSolutionEvaluation() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return "";
        }
        if (evaluationSchema == null) {
            JsfUtil.addErrorMessage("Select Evaluation Scehma");
            return "";
        }
        solutionEvaluationSchema = new SolutionEvaluationSchema();
        solutionEvaluationSchema.setSolution(selected);
        solutionEvaluationSchema.setEvaluationSchema(evaluationSchema);
        solutionEvaluationSchema.setCreatedAt(new Date());
        solutionEvaluationSchema.setCreatedBy(webUserController.getLoggedUser());
        getSesFacade().create(solutionEvaluationSchema);

        Poe poe = new Poe();
        poe.setEvaluationSchema(evaluationSchema);
        poe.setSolution(selected);

        String j;
        Map m = new HashMap();

        List<EvaluationGroup> egs;

        j = "Select eg "
                + " from EvaluationGroup eg "
                + " where eg.retired=:ret "
                + " and eg.evaluationSchema=:es "
                + " order by eg.orderNo";
        m.put("ret", false);
        m.put("es", solutionEvaluationSchema);
        egs = getEgFacade().findByJpql(j, m);
        double onSeg = 0.0;
        for (EvaluationGroup eg : egs) {
            SolutionEvaluationGroup seg = new SolutionEvaluationGroup();
            seg.setEvaluationGroup(eg);
            seg.setCreatedAt(new Date());
            seg.setCreatedBy(webUserController.getLoggedUser());
            seg.setOrderNo(onSeg);
            getSegFacade().create(seg);

            Poeg poeg = new Poeg();
            poeg.setEvaluationGroup(eg);
            poeg.setSolutionEvaluationGroup(seg);

            double onSei = 0.0;
            List<EvaluationItem> eis;

            j = "select ei from EvaluationItem ei "
                    + " where ei.retired=:ret "
                    + " and ei.evaluationGroup=:eg "
                    + " and ei.parent is null "
                    + " order by ei.orderNo";
            m = new HashMap();
            m.put("ret", false);
            m.put("eg", eg);
            eis = getEiFacade().findByJpql(j, m);

            for (EvaluationItem ei : eis) {
                j = "select ei from EvaluationItem ei "
                        + " where ei.retired=:ret "
                        + " and ei.parent=:ei "
                        + " order by ei.orderNo";
                m = new HashMap();
                m.put("ret", false);
                m.put("eg", eg);
                
                SolutionEvaluationItem sei = new SolutionEvaluationItem();
                sei.setEvaluationItem(ei);
                sei.setSolutionEvaluationGroup(seg);
                sei.setCreatedAt(new Date());
                sei.setCreatedBy(webUserController.getLoggedUser());
                sei.setOrderNo(onSei);
                getSeiFacade().create(sei);
                
                PoEi poei = new PoEi();
                poei.setEvaluationItem(ei);
                poei.setSolutionEvaluationItem(sei);
                
                

                List<EvaluationItem> seis = getEiFacade().findByJpql(j, m);
                if (seis != null && seis.size() > 0) {
                    double onCsei =0.0;
                    for (EvaluationItem cei : seis) {
                        SolutionEvaluationItem csei = new SolutionEvaluationItem();
                        csei.setParent(sei);
                        csei.setEvaluationItem(cei);
                        csei.setCreatedAt(new Date());
                        csei.setCreatedBy(webUserController.getLoggedUser());
                        csei.setOrderNo(onCsei);
                        getSeiFacade().create(csei);
                        
                        PoEi spoei = new PoEi();
                        spoei.setEvaluationItem(cei);
                        spoei.setSolutionEvaluationItem(csei);
                        
                        

                    }

                }
                
                poeg.getPoeis().put(ei.getId(), poei);
                onSei++;

            }

            poe.getPoegs().put(eg.getId(), poeg);
            onSeg++;
        }

        return "/solution/evaluation";
    }

    public String toListSolution() {
        selected = null;
        String j = "select s from Solution s where s.retired=:ret order by  s.name";
        Map m = new HashMap();
        m.put("ret", false);
        items = getFacade().findByJpql(j, m);
        return "/solution/list";
    }

    public Solution getSelected() {
        return selected;
    }

    public void setSelected(Solution selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SolutionFacade getFacade() {
        return facade;
    }

    public Solution prepareCreate() {
        selected = new Solution();
        initializeEmbeddableKey();
        return selected;
    }

    public void save() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        if (selected.getId() == null) {
            selected.setCreatedAt(new Date());
            selected.setCreatedBy(webUserController.getLoggedUser());
            getFacade().create(selected);
            JsfUtil.addSuccessMessage("Saved");
        } else {
            selected.setLastEditedAt(new Date());
            selected.setLastEditedBy(webUserController.getLoggedUser());
            getFacade().edit(selected);
            JsfUtil.addSuccessMessage("Updated");
        }
        items = null;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleP4ppp1").getString("SolutionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Solution> getItems() {
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

    public Solution getSolution(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Solution> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Solution> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public SolutionEvaluationSchemeFacade getSesFacade() {
        return sesFacade;
    }

    public SolutionEvaluationGroupFacade getSegFacade() {
        return segFacade;
    }

    public SolutionEvaluationItemFacade getSeiFacade() {
        return seiFacade;
    }

    public EvaluationSchemaFacade getEsFacade() {
        return esFacade;
    }

    public EvaluationGroupFacade getEgFacade() {
        return egFacade;
    }

    public EvaluationItemFacade getEiFacade() {
        return eiFacade;
    }

    public List<SolutionEvaluationSchema> getSolutionEvaluationSchemas() {
        return solutionEvaluationSchemas;
    }

    public void setSolutionEvaluationSchemas(List<SolutionEvaluationSchema> solutionEvaluationSchemas) {
        this.solutionEvaluationSchemas = solutionEvaluationSchemas;
    }

    public SolutionEvaluationSchema getSolutionEvaluationSchema() {
        return solutionEvaluationSchema;
    }

    public void setSolutionEvaluationSchema(SolutionEvaluationSchema solutionEvaluationSchema) {
        this.solutionEvaluationSchema = solutionEvaluationSchema;
    }

    public EvaluationSchema getEvaluationSchema() {
        return evaluationSchema;
    }

    public void setEvaluationSchema(EvaluationSchema evaluationSchema) {
        this.evaluationSchema = evaluationSchema;
    }

    @FacesConverter(forClass = Solution.class)
    public static class SolutionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SolutionController controller = (SolutionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "solutionController");
            return controller.getSolution(getKey(value));
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
            if (object instanceof Solution) {
                Solution o = (Solution) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Solution.class.getName()});
                return null;
            }
        }

    }

}
