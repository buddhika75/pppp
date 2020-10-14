package cwcdh.pppp.bean;

import cwcdh.pppp.entity.Solution;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.entity.EvaluationGroup;
import cwcdh.pppp.entity.EvaluationItem;
import cwcdh.pppp.entity.EvaluationSchema;
import cwcdh.pppp.entity.Item;
import cwcdh.pppp.entity.SolutionEvaluationGroup;
import cwcdh.pppp.entity.SolutionEvaluationItem;
import cwcdh.pppp.entity.SolutionEvaluationSchema;
import cwcdh.pppp.entity.SolutionItem;
import cwcdh.pppp.entity.WebUser;
import cwcdh.pppp.enums.DataType;
import cwcdh.pppp.enums.MultipleItemCalculationMethod;
import cwcdh.pppp.enums.P4PPPCategory;
import cwcdh.pppp.enums.Placeholder;
import cwcdh.pppp.facade.EvaluationGroupFacade;
import cwcdh.pppp.facade.EvaluationItemFacade;
import cwcdh.pppp.facade.EvaluationSchemaFacade;
import cwcdh.pppp.facade.SolutionEvaluationGroupFacade;
import cwcdh.pppp.facade.SolutionEvaluationItemFacade;
import cwcdh.pppp.facade.SolutionEvaluationSchemeFacade;
import cwcdh.pppp.facade.SolutionFacade;
import cwcdh.pppp.facade.SolutionItemFacade;
import cwcdh.pppp.pojcs.PoEi;
import cwcdh.pppp.pojcs.PoItem;
import cwcdh.pppp.pojcs.Poe;
import cwcdh.pppp.pojcs.Poeg;
import cwcdh.pppp.pojcs.Display;
import cwcdh.pppp.pojcs.DisplayItem;
import cwcdh.pppp.pojcs.DisplayItemType;
import cwcdh.pppp.pojcs.DisplayPlaceholder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    private SolutionItemFacade siFacade;
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
    private List<SolutionEvaluationSchema> solutionProfiles;
    private SolutionEvaluationSchema solutionEvaluationSchema;
    private SolutionEvaluationSchema solutionProfile;
    private SolutionEvaluationSchema viewingSolutionProfile;
    private Solution viewingSolution;
    private EvaluationSchema evaluationSchema;
    private Poe selectedPoe;
    private Poe viewingPoe;
    private Display viewingDisplay;
    private WebUser user;
    private String comments;
    private EvaluationItem newEi;

    private boolean assignData;
    private boolean acceptanceData;
    private boolean rejecData;
    private boolean completeData;
    private boolean enrollData;
    private boolean scoreData;

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

    public String toListSolutionProfiles() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to List");
            return "";
        }
        String j;
        Map m = new HashMap();
        j = "Select ses from SolutionEvaluationSchema ses "
                + " where ses.retired=:ret "
                + " and ses.solution=:sol "
                + " and ses.frontEndDetail=:fed"
                + " order by ses.orderNo";

        m.put("ret", false);
        m.put("fed", true);
        m.put("sol", selected);
        if (false) {
            SolutionEvaluationSchema s = new SolutionEvaluationSchema();
            s.getSolution();
            s.isRetired();
            s.isFrontEndDetail();
        }
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/evaluations";
    }

    public String toSolutionProfiles() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return "";
        }
        String j;
        Map m = new HashMap();
        j = "Select ses from SolutionEvaluationSchema ses "
                + " where ses.retired=:ret "
                + " and ses.solution=:sol "
                + " and ses.frontEndDetail=:fed "
                + " order by ses.orderNo";

        m.put("ret", false);
        m.put("fed", true);
        m.put("sol", selected);
        solutionProfiles = getSesFacade().findByJpql(j, m);
        return "/solution/profiles";
    }

    public String toViewSolutionEvaluation() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Select Evaluation Scehma");
            return "";
        }
        toEditSolutionEvaluation();
        return "/solution/evaluation_view";
    }

    public String toStartMySolutionEvaluation() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Select Evaluation Scehma");
            return "";
        }

        solutionEvaluationSchema.setAccepted(true);
        if (solutionEvaluationSchema.getAcceptedAt() == null) {
            solutionEvaluationSchema.setAcceptedAt(new Date());
        }

        return toEditSolutionEvaluation();
    }

    public String toMyEvaluations() {
        solutionEvaluationSchemas = null;
        getSolutionEvaluationSchemas();
        return "/solution/my_evaluations";
    }

    public String toAcceptMySolutionEvaluation() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Select Evaluation Scehma");
            return "";
        }
        solutionEvaluationSchema.setAccepted(true);
        solutionEvaluationSchema.setAcceptComments(comments);
        if (solutionEvaluationSchema.getAcceptedAt() == null) {
            solutionEvaluationSchema.setAcceptedAt(new Date());
        }
        getSesFacade().edit(solutionEvaluationSchema);
        return toMyEvaluations();
    }

    public String toRejectMySolutionEvaluation() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Select Evaluation Scehma");
            return "";
        }
        solutionEvaluationSchema.setAccepted(false);
        solutionEvaluationSchema.setRejected(true);
        solutionEvaluationSchema.setRejectedAt(new Date());
        solutionEvaluationSchema.setRejectionComments(comments);
        getSesFacade().edit(solutionEvaluationSchema);
        return toMyEvaluations();
    }

    public String toCompleteMySolutionEvaluation() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Select Evaluation Scehma");
            return "";
        }
        solutionEvaluationSchema.setCompleted(true);
        solutionEvaluationSchema.setCompletedAt(new Date());
        getSesFacade().edit(solutionEvaluationSchema);
        return toMyEvaluations();
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

        return toEditSolutionEvaluation();
    }

    public String toEditSolutionEvaluation() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return "";
        }

        evaluationSchema = solutionEvaluationSchema.getEvaluationSchema();

        String j;
        Map m = new HashMap();

        Poe poe = new Poe();
        poe.setEvaluationSchema(solutionEvaluationSchema.getEvaluationSchema());
        poe.setSolution(solutionEvaluationSchema.getSolution());
        poe.setSolutionEvaluationSchema(solutionEvaluationSchema);

        List<EvaluationGroup> egs = findEvaluationGroupsOfevaluationSchema(evaluationSchema);

        double onSeg = 0.0;

        for (EvaluationGroup eg : egs) {
            SolutionEvaluationGroup seg = findSolutionEvaluationGroup(solutionEvaluationSchema, eg);
            seg.setOrderNo(onSeg);

            Poeg poeg = new Poeg();
            poeg.setEvaluationGroup(eg);
            poeg.setSolutionEvaluationGroup(seg);

            double onSei = 0.0;
            List<EvaluationItem> eis = findRootEvalautionItemsOfEvaluationGroup(eg);

            if (eis != null && !eis.isEmpty()) {
                for (EvaluationItem ei : eis) {
                    SolutionEvaluationItem sei = findSolutionEvaluationItem(ei, seg);
                    sei.setOrderNo(onSei);

                    PoEi poei = new PoEi();
                    poei.setEvaluationItem(ei);
                    poei.setSolutionEvaluationItem(sei);

                    List<EvaluationItem> childrenOfEvaluationItem = findChildrenOfEvaluationItem(ei);
                    if (childrenOfEvaluationItem != null && childrenOfEvaluationItem.size() > 0) {
                        double onCsei = 0.0;
                        for (EvaluationItem cei : childrenOfEvaluationItem) {

                            SolutionEvaluationItem csei = findChildSolutionEvaluationItem(cei, sei);
                            csei.setOrderNo(onCsei);

                            List<SolutionItem> sis = findSolutionItems(csei);

                            PoEi spoei = new PoEi();
                            spoei.setEvaluationItem(cei);
                            spoei.setSolutionEvaluationItem(csei);

                            for (SolutionItem tsi : sis) {

                                PoItem poi = new PoItem();
                                poi.setSolutionItem(tsi);
                                poi.setPoei(spoei);

                                spoei.getPoItems().add(poi);

                            }
                            poei.getSubEis().put(cei.getId(), spoei);
                            onCsei++;

                        }

                    } else {

                        List<SolutionItem> sis = findSolutionItems(sei);
                        for (SolutionItem tsi : sis) {
                            PoItem poi = new PoItem();
                            poi.setSolutionItem(tsi);
                            poi.setPoei(poei);

                            poei.getPoItems().add(poi);
                        }

                    }

                    poeg.getPoeis().put(ei.getId(), poei);
                    onSei++;

                }
            }

            poe.getPoegs().put(eg.getId(), poeg);
            onSeg++;
        }
        selectedPoe = poe;
        return "/solution/evaluation";
    }

    public String toRemoveSolutionEvaluation() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return "";
        }
        solutionEvaluationSchema.setRetired(true);
        solutionEvaluationSchema.setRetiredAt(new Date());
        solutionEvaluationSchema.setRetiredBy(webUserController.getLoggedUser());
        getSesFacade().edit(solutionEvaluationSchema);
        solutionEvaluationSchemas = null;
        return toSolutionEvaluations();
    }

    public String toRemoveSolutionProfile() {
        if (solutionEvaluationSchema == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return "";
        }
        solutionEvaluationSchema.setRetired(true);
        solutionEvaluationSchema.setRetiredAt(new Date());
        solutionEvaluationSchema.setRetiredBy(webUserController.getLoggedUser());
        getSesFacade().edit(solutionEvaluationSchema);
        solutionEvaluationSchemas = null;
        return toSolutionProfiles();
    }

    public String toNewSolutionEvaluationOld() {
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
        poe.setSolutionEvaluationSchema(solutionEvaluationSchema);

        List<EvaluationGroup> egs = findEvaluationGroupsOfevaluationSchema(evaluationSchema);

        double onSeg = 0.0;
        for (EvaluationGroup eg : egs) {
            SolutionEvaluationGroup seg = new SolutionEvaluationGroup();
            seg.setEvaluationGroup(eg);
            seg.setCreatedAt(new Date());
            seg.setSolutionEvaluationScheme(solutionEvaluationSchema);
            seg.setCreatedBy(webUserController.getLoggedUser());
            seg.setOrderNo(onSeg);
            getSegFacade().create(seg);

            Poeg poeg = new Poeg();
            poeg.setEvaluationGroup(eg);
            poeg.setSolutionEvaluationGroup(seg);

            double onSei = 0.0;
            List<EvaluationItem> eis = findRootEvalautionItemsOfEvaluationGroup(eg);

            if (eis != null && !eis.isEmpty()) {

                for (EvaluationItem ei : eis) {

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

                    List<EvaluationItem> childrenOfEvaluationItem = findChildrenOfEvaluationItem(ei);

                    if (childrenOfEvaluationItem != null && childrenOfEvaluationItem.size() > 0) {
                        double onCsei = 0.0;
                        for (EvaluationItem childEvaluationItem : childrenOfEvaluationItem) {
                            SolutionEvaluationItem csei = new SolutionEvaluationItem();
                            csei.setParent(sei);
                            csei.setEvaluationItem(childEvaluationItem);
                            csei.setCreatedAt(new Date());
                            csei.setCreatedBy(webUserController.getLoggedUser());
                            csei.setOrderNo(onCsei);
                            getSeiFacade().create(csei);

                            SolutionItem si = new SolutionItem();
                            si.setEvaluationItem(childEvaluationItem);
                            si.setSolutionEvaluationItem(csei);
                            si.setCreatedAt(new Date());
                            si.setCreatedBy(webUserController.getLoggedUser());
                            si.setOrderNo(onCsei);
                            getSiFacade().create(si);

                            PoEi spoei = new PoEi();
                            spoei.setEvaluationItem(childEvaluationItem);
                            spoei.setSolutionEvaluationItem(csei);

                            PoItem poi = new PoItem();
                            poi.setSolutionItem(si);
                            poi.setPoei(spoei);

                            spoei.getPoItems().add(poi);

                            poei.getSubEis().put(childEvaluationItem.getId(), spoei);
                            onCsei++;
                        }

                    } else {
                        SolutionItem si = new SolutionItem();
                        si.setEvaluationItem(ei);
                        si.setSolutionEvaluationItem(sei);
                        si.setCreatedAt(new Date());
                        si.setCreatedBy(webUserController.getLoggedUser());
                        si.setOrderNo(onSei);
                        getSiFacade().create(si);

                        PoItem poi = new PoItem();
                        poi.setSolutionItem(si);
                        poi.setPoei(poei);

                        poei.getPoItems().add(poi);

                    }

                    poeg.getPoeis().put(ei.getId(), poei);
                    onSei++;

                }
            }

            poe.getPoegs().put(eg.getId(), poeg);
            onSeg++;
        }
        selectedPoe = poe;
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

    public List<Solution> completeSolution(String qry) {
        if (qry == null) {
            return new ArrayList<>();
        }
        String j = "select s "
                + " from Solution s "
                + " where s.retired=:ret "
                + " and lower(s.name) like :qry "
                + " order by  s.name";
        Map m = new HashMap();
        m.put("ret", false);
        m.put("qry", "%" + qry.trim().toLowerCase() + "%");
        return getFacade().findByJpql(j, m, 30);
    }

    public String toCreatSolutionProfile() {
        return "/solution/create_profile";
    }

    public String toAssignSolution() {
        return "/solution/assign";
    }

    public String toManageSolutionEvaluations() {
        return "/solution/manage_evaluations";
    }

    public String toConsolidateEaluations() {
        return "/solution/consolidate_evaluations";
    }

    public List<EvaluationGroup> findEvaluationGroupsOfevaluationSchema(EvaluationSchema evaluationSchema) {
        String j;
        Map m = new HashMap();
        List<EvaluationGroup> egs;
        j = "Select eg "
                + " from EvaluationGroup eg "
                + " where eg.retired=:ret "
                + " and eg.evaluationSchema=:es "
                + " order by eg.orderNo";
        m.put("ret", false);
        m.put("es", evaluationSchema);
        egs = getEgFacade().findByJpql(j, m);
        return egs;
    }

    public List<EvaluationGroup> findEvaluationGroupsOfevaluationSchemaForProfiling(EvaluationSchema evaluationSchema) {
        System.out.println("findEvaluationGroupsOfevaluationSchemaForProfiling");
        String j;
        Map m = new HashMap();
        List<EvaluationGroup> egs;
        j = "Select eg "
                + " from EvaluationGroup eg "
                + " where eg.retired=:ret "
                + " and eg.evaluationSchema=:es "
                + " and eg.usedForProfiling=:pro "
                + " order by eg.orderNo";
        m.put("ret", false);
        m.put("pro", true);
        m.put("es", evaluationSchema);
        egs = getEgFacade().findByJpql(j, m);
        return egs;
    }

    public List<EvaluationItem> findRootEvalautionItemsOfEvaluationGroup(EvaluationGroup evaluationGroup) {
        List<EvaluationItem> eis;
        String j;
        Map m = new HashMap();
        j = "select ei from EvaluationItem ei "
                + " where ei.retired=:ret "
                + " and ei.evaluationGroup=:eg "
                + " and ei.parent is null "
                + " order by ei.orderNo";
        m.put("ret", false);
        m.put("eg", evaluationGroup);
        eis = getEiFacade().findByJpql(j, m);
        return eis;
    }

    public List<EvaluationItem> findRootEvalautionItemsOfEvaluationGroupForProfiling(EvaluationGroup evaluationGroup) {
        List<EvaluationItem> eis;
        String j;
        Map m = new HashMap();
        j = "select ei from EvaluationItem ei "
                + " where ei.retired=:ret "
                + " and ei.evaluationGroup=:eg "
                + " and ei.usedForProfiling=:pro"
                + " and ei.parent is null "
                + " order by ei.orderNo";
        m.put("ret", false);
        m.put("pro", true);
        m.put("eg", evaluationGroup);
        eis = getEiFacade().findByJpql(j, m);
        return eis;
    }

    public List<EvaluationItem> findChildrenOfEvaluationItem(EvaluationItem evaluationItem) {
        List<EvaluationItem> eis;
        String j;
        Map m = new HashMap();
        j = "select ei from EvaluationItem ei "
                + " where ei.retired=:ret "
                + " and ei.parent=:ei "
                + " order by ei.orderNo";
        m.put("ret", false);
        m.put("ei", evaluationItem);
        eis = getEiFacade().findByJpql(j, m);
        return eis;
    }

    public List<EvaluationItem> findChildrenOfEvaluationItemForProfiling(EvaluationItem evaluationItem) {
        List<EvaluationItem> eis;
        String j;
        Map m = new HashMap();
        j = "select ei from EvaluationItem ei "
                + " where ei.retired=:ret "
                + " and ei.parent=:ei "
                + " and ei.usedForProfiling=:pro "
                + " order by ei.orderNo";
        m.put("ret", false);
        m.put("pro", true);
        m.put("ei", evaluationItem);
        eis = getEiFacade().findByJpql(j, m);
        return eis;
    }

    public SolutionEvaluationGroup findSolutionEvaluationGroup(SolutionEvaluationSchema solutionEvaluationSchema, EvaluationGroup evaluationGroup) {
        String j;
        Map m = new HashMap();
        SolutionEvaluationGroup seg;
        j = "Select seg "
                + " from SolutionEvaluationGroup seg "
                + " where seg.retired<>:ret "
                + " and seg.evaluationGroup=:eg "
                + " and seg.solutionEvaluationScheme=:ses";
        m.put("ret", true);
        m.put("eg", evaluationGroup);
        m.put("ses", solutionEvaluationSchema);
        seg = getSegFacade().findFirstByJpql(j, m);
        if (seg == null) {
            seg = new SolutionEvaluationGroup();
            seg.setEvaluationGroup(evaluationGroup);
            seg.setSolutionEvaluationScheme(solutionEvaluationSchema);
            seg.setCreatedAt(new Date());
            seg.setCreatedBy(webUserController.getLoggedUser());
            getSegFacade().create(seg);
        }
        return seg;
    }

    public SolutionEvaluationItem createSolutionEvaluationItem(EvaluationItem evaluationItem, SolutionEvaluationGroup solutionEvaluationGroup) {
        SolutionEvaluationItem sei;
        sei = new SolutionEvaluationItem();
        sei.setEvaluationItem(evaluationItem);
        sei.setSolutionEvaluationGroup(solutionEvaluationGroup);
        sei.setCreatedAt(new Date());
        sei.setCreatedBy(webUserController.getLoggedUser());
        getSeiFacade().create(sei);
        return sei;
    }

    public SolutionEvaluationItem findSolutionEvaluationItem(EvaluationItem evaluationItem, SolutionEvaluationGroup solutionEvaluationGroup) {
        String j;
        Map m = new HashMap();
        j = "select sei from SolutionEvaluationItem sei "
                + " where sei.retired=:ret "
                + " and sei.evaluationItem=:ei "
                + " and sei.solutionEvaluationGroup=:seg "
                + " order by sei.id desc";
        m.put("ret", false);
        m.put("ei", evaluationItem);
        m.put("seg", solutionEvaluationGroup);

        SolutionEvaluationItem sei;

        sei = getSeiFacade().findFirstByJpql(j, m);

        if (sei == null) {
            sei = new SolutionEvaluationItem();
            sei.setEvaluationItem(evaluationItem);
            sei.setSolutionEvaluationGroup(solutionEvaluationGroup);
            sei.setCreatedAt(new Date());
            sei.setCreatedBy(webUserController.getLoggedUser());
            getSeiFacade().create(sei);
        }
        return sei;

    }

    public SolutionEvaluationItem findChildSolutionEvaluationItem(EvaluationItem evaluationItem, SolutionEvaluationItem solutionEvaluationItem) {
        String j;
        Map m = new HashMap();
        j = "select sei from SolutionEvaluationItem sei "
                + " where sei.retired=:ret "
                + " and sei.evaluationItem=:ei "
                + " and sei.parent=:p";
        m.put("ret", false);
        m.put("ei", evaluationItem);
        m.put("p", solutionEvaluationItem);

        SolutionEvaluationItem csei;

        csei = getSeiFacade().findFirstByJpql(j, m);

        if (csei == null) {
            csei = new SolutionEvaluationItem();
            csei.setParent(solutionEvaluationItem);
            csei.setEvaluationItem(evaluationItem);
            csei.setCreatedAt(new Date());
            csei.setCreatedBy(webUserController.getLoggedUser());
            getSeiFacade().create(csei);
        }
        return csei;
    }

    public List<SolutionItem> findSolutionItems(SolutionEvaluationItem solutionEvaluationItem) {
        String j;
        Map m = new HashMap();
        j = "select si from SolutionItem si "
                + " where si.retired=:ret "
                + " and si.solutionEvaluationItem=:sei "
                + " order by si.orderNo";
        m.put("ret", false);
        m.put("sei", solutionEvaluationItem);

        List<SolutionItem> sis = getSiFacade().findByJpql(j, m);
        if (sis == null) {
            sis = new ArrayList();
        }
        if (sis.isEmpty()) {
            SolutionItem si = new SolutionItem();
            si.setSolutionEvaluationItem(solutionEvaluationItem);
            si.setCreatedAt(new Date());
            si.setCreatedBy(webUserController.getLoggedUser());
            getSiFacade().create(si);
            sis.add(si);
        }
        return sis;
    }

    private double findScore(PoItem poitem) {
        double score = 0.0;
        if (poitem == null) {
            return score;
        }
        SolutionItem si = poitem.getSolutionItem();
        if (si == null) {
            return score;
        }
        Item i = si.getItemValue();
        if (i == null) {
            return score;
        }
        if (i.getScoreValue() == null) {
            return score;
        }
        score = i.getScoreValue();
        return score;
    }

    private double findSolutionEvaluationItemScore(PoEi poei) {
        double score = 0.0;
        EvaluationItem ei = poei.getEvaluationItem();
        if (ei == null) {
            return score;
        }
        if (ei.getDataType() != DataType.Item) {
            return score;
        }
        if (poei.getPoItems().size() < 1) {
            return score;
        } else if (poei.getPoItems().size() == 1) {
            score = findScore(poei.getPoItems().get(0));
            return score;
        }
        for (PoItem i : poei.getPoItems()) {
            if (ei.getMultipleItemCalculationMethod() == null) {
                ei.setMultipleItemCalculationMethod(MultipleItemCalculationMethod.Highest);
            }
            double temScore = findScore(i);
            switch (ei.getMultipleItemCalculationMethod()) {
                case Highest:
                    if (temScore > score) {
                        score = temScore;
                    }
                    break;
                case Lowest:
                    if (temScore < score) {
                        score = temScore;
                    }
                    break;
                case Mean:
                    score += temScore;
                    break;
                default:
                    break;
            }

        }

        if (ei.getMultipleItemCalculationMethod() == MultipleItemCalculationMethod.Mean) {
            score = score / poei.getPoItems().size();
        }

        return score;
    }

    public void calculateScores(Poe poe) {
        double solutionScore;
        double solutionGroupScore;
        double solutionRootElementScore;
        double solutionChildElementScore;
        solutionScore = 0.0;

        for (Poeg poeg : poe.getPoegsList()) {
            solutionGroupScore = 0.0;

            for (PoEi poei : poeg.getPoeisList()) {
                solutionRootElementScore = 0.0;
                if (poei.getSubEisList().isEmpty()) {
                    solutionRootElementScore = findSolutionEvaluationItemScore(poei);
                    System.out.println("solutionRootElementScore = " + solutionRootElementScore);
                } else {
                    System.out.println("Still to calculate");
                }

                double temWeigtage = poei.getEvaluationItem().getWeight();

                solutionGroupScore += (solutionRootElementScore * temWeigtage);

                poei.getSolutionEvaluationItem().setScore(solutionRootElementScore);

                getSeiFacade().edit(poei.getSolutionEvaluationItem());

            }
            System.out.println("solutionGroupScore = " + solutionGroupScore);
            poeg.getSolutionEvaluationGroup().setScore(solutionGroupScore);
            getSegFacade().edit(poeg.getSolutionEvaluationGroup());

            solutionScore += solutionGroupScore;
        }

        poe.getSolutionEvaluationSchema().setScore(solutionScore);
        getSesFacade().edit(poe.getSolutionEvaluationSchema());
    }

    public void saveSelectedProfile() {
        if (selectedPoe == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        if (selectedPoe.getSolutionEvaluationSchema() == null) {
            JsfUtil.addErrorMessage("Can not save");
            return;
        }

        if (selectedPoe.getSolutionEvaluationSchema().getId() == null) {
            selectedPoe.getSolutionEvaluationSchema().setCreatedAt(new Date());
            selectedPoe.getSolutionEvaluationSchema().setCreatedBy(webUserController.getLoggedUser());
            getSesFacade().create(selectedPoe.getSolutionEvaluationSchema());
        } else {
            selectedPoe.getSolutionEvaluationSchema().setLastEditedAt(new Date());
            selectedPoe.getSolutionEvaluationSchema().setLastEditedBy(webUserController.getLoggedUser());
            getSesFacade().edit(selectedPoe.getSolutionEvaluationSchema());
        }

        for (Poeg poeg : selectedPoe.getPoegsList()) {
            if (poeg.getSolutionEvaluationGroup() == null) {
                JsfUtil.addErrorMessage("Nothing to save at group level");
                return;
            }
            if (poeg.getSolutionEvaluationGroup().getId() == null) {
                poeg.getSolutionEvaluationGroup().setCreatedAt(new Date());
                poeg.getSolutionEvaluationGroup().setCreatedBy(webUserController.getLoggedUser());
                getSegFacade().create(poeg.getSolutionEvaluationGroup());
            } else {
                poeg.getSolutionEvaluationGroup().setLastEditedAt(new Date());
                poeg.getSolutionEvaluationGroup().setLastEditedBy(webUserController.getLoggedUser());
                getSegFacade().edit(poeg.getSolutionEvaluationGroup());
            }
            for (PoEi poei : poeg.getPoeisList()) {
                SolutionEvaluationItem sei = poei.getSolutionEvaluationItem();

                if (sei != null) {
                    if (sei.getId() == null) {
                        sei.setCreatedAt(new Date());
                        sei.setCreatedBy(webUserController.getLoggedUser());
                        getSeiFacade().create(sei);
                    } else {
                        sei.setLastEditedAt(new Date());
                        sei.setLastEditedBy(webUserController.getLoggedUser());
                        getSeiFacade().edit(sei);
                    }
                }
                for (PoItem poi : poei.getPoItems()) {
                    SolutionItem si = poi.getSolutionItem();

                    if (si != null) {
                        System.out.println("si = " + si.getShortTextValue());
                        if (si.getId() == null) {
                            si.setCreatedAt(new Date());
                            si.setCreatedBy(webUserController.getLoggedUser());
                            getSiFacade().create(si);
                        } else {
                            si.setLastEditedAt(new Date());
                            si.setLastEditedBy(webUserController.getLoggedUser());
                            getSiFacade().edit(si);
                        }
                    }
                }

                for (PoEi spoei : poei.getSubEisList()) {
                    SolutionEvaluationItem ssei = spoei.getSolutionEvaluationItem();
                    if (ssei != null) {
                        if (ssei.getId() == null) {
                            ssei.setCreatedAt(new Date());
                            ssei.setCreatedBy(webUserController.getLoggedUser());
                            getSeiFacade().create(ssei);
                        } else {
                            ssei.setLastEditedAt(new Date());
                            ssei.setLastEditedBy(webUserController.getLoggedUser());
                            getSeiFacade().edit(ssei);
                        }
                    }
                    for (PoItem spoi : spoei.getPoItems()) {
                        SolutionItem ssi = spoi.getSolutionItem();
                        if (ssi != null) {
                            if (ssi.getId() == null) {
                                ssi.setCreatedAt(new Date());
                                ssi.setCreatedBy(webUserController.getLoggedUser());
                                getSiFacade().create(ssi);
                            } else {
                                ssi.setLastEditedAt(new Date());
                                ssi.setLastEditedBy(webUserController.getLoggedUser());
                                getSiFacade().edit(ssi);
                            }
                        }
                    }

                }

            }
        }

        JsfUtil.addSuccessMessage("Saved Successfully");
    }

    public void saveSelectedEvaluation() {
        if (selectedPoe == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        if (selectedPoe.getSolutionEvaluationSchema() == null) {
            JsfUtil.addErrorMessage("Can not save");
            return;
        }

        if (selectedPoe.getSolutionEvaluationSchema().getId() == null) {
            selectedPoe.getSolutionEvaluationSchema().setCreatedAt(new Date());
            selectedPoe.getSolutionEvaluationSchema().setCreatedBy(webUserController.getLoggedUser());
            getSesFacade().create(selectedPoe.getSolutionEvaluationSchema());
        } else {
            selectedPoe.getSolutionEvaluationSchema().setLastEditedAt(new Date());
            selectedPoe.getSolutionEvaluationSchema().setLastEditedBy(webUserController.getLoggedUser());
            getSesFacade().edit(selectedPoe.getSolutionEvaluationSchema());
        }

        for (Poeg poeg : selectedPoe.getPoegsList()) {
            if (poeg.getSolutionEvaluationGroup() == null) {
                JsfUtil.addErrorMessage("Nothing to save at group level");
                return;
            }
            if (poeg.getSolutionEvaluationGroup().getId() == null) {
                poeg.getSolutionEvaluationGroup().setCreatedAt(new Date());
                poeg.getSolutionEvaluationGroup().setCreatedBy(webUserController.getLoggedUser());
                getSegFacade().create(poeg.getSolutionEvaluationGroup());
            } else {
                poeg.getSolutionEvaluationGroup().setLastEditedAt(new Date());
                poeg.getSolutionEvaluationGroup().setLastEditedBy(webUserController.getLoggedUser());
                getSegFacade().edit(poeg.getSolutionEvaluationGroup());
            }
            for (PoEi poei : poeg.getPoeisList()) {
                SolutionEvaluationItem sei = poei.getSolutionEvaluationItem();
                if (sei != null) {
                    if (sei.getId() == null) {
                        sei.setCreatedAt(new Date());
                        sei.setCreatedBy(webUserController.getLoggedUser());
                        getSeiFacade().create(sei);
                    } else {
                        sei.setLastEditedAt(new Date());
                        sei.setLastEditedBy(webUserController.getLoggedUser());
                        getSeiFacade().edit(sei);
                    }
                }
                for (PoItem poi : poei.getPoItems()) {
                    SolutionItem si = poi.getSolutionItem();
                    if (si != null) {
                        if (si.getId() == null) {
                            si.setCreatedAt(new Date());
                            si.setCreatedBy(webUserController.getLoggedUser());
                            getSiFacade().create(si);
                        } else {
                            si.setLastEditedAt(new Date());
                            si.setLastEditedBy(webUserController.getLoggedUser());
                            getSiFacade().edit(si);
                        }
                    }
                }

                for (PoEi spoei : poei.getSubEisList()) {
                    SolutionEvaluationItem ssei = spoei.getSolutionEvaluationItem();
                    if (ssei != null) {
                        if (ssei.getId() == null) {
                            ssei.setCreatedAt(new Date());
                            ssei.setCreatedBy(webUserController.getLoggedUser());
                            getSeiFacade().create(ssei);
                        } else {
                            ssei.setLastEditedAt(new Date());
                            ssei.setLastEditedBy(webUserController.getLoggedUser());
                            getSeiFacade().edit(ssei);
                        }
                    }
                    for (PoItem spoi : spoei.getPoItems()) {
                        SolutionItem ssi = spoi.getSolutionItem();
                        if (ssi != null) {
                            if (ssi.getId() == null) {
                                ssi.setCreatedAt(new Date());
                                ssi.setCreatedBy(webUserController.getLoggedUser());
                                getSiFacade().create(ssi);
                            } else {
                                ssi.setLastEditedAt(new Date());
                                ssi.setLastEditedBy(webUserController.getLoggedUser());
                                getSiFacade().edit(ssi);
                            }
                        }
                    }

                }

            }
        }
        calculateScores(selectedPoe);
        JsfUtil.addSuccessMessage("Saved Successfully");
    }

    public String assignEvaluation() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Select a solution");
            return "";
        }
        if (evaluationSchema == null) {
            JsfUtil.addErrorMessage("Select a schema");
            return "";
        }
        if (user == null) {
            JsfUtil.addErrorMessage("Select a User");
            return "";
        }
        if (comments == null) {
            JsfUtil.addErrorMessage("Please enter a message");
            return "";
        }
        SolutionEvaluationSchema sec = new SolutionEvaluationSchema();
        sec.setCreatedAt(new Date());
        sec.setCreatedBy(webUserController.getLoggedUser());
        sec.setAssigned(true);
        sec.setAssignComments(comments);
        sec.setSolution(selected);
        sec.setEvaluationSchema(evaluationSchema);
        sec.setAssignedBy(webUserController.getLoggedUser());
        sec.setAssignedAt(new Date());
        sec.setEvaluationBy(user);
        getSesFacade().create(sec);
        JsfUtil.addSuccessMessage("Successfully Assigned");
        comments = "";
        user = null;
        return toAssignSolution();

    }

    public String createProfileEvaluation() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Select a solution");
            return "";
        }
        if (evaluationSchema == null) {
            JsfUtil.addErrorMessage("Select a schema");
            return "";
        }
        SolutionEvaluationSchema sec = null;
        String jpql = "select se "
                + " from SolutionEvaluationSchema "
                + " where se.solution=:sol "
                + " and se.evaluationSchema=:es "
                + " and se.frontEndDetail=:fed";
        Map m = new HashMap();
        m.put("sol", selected);
        m.put("fed", true);
        m.put("es", evaluationSchema);
        sec = getSesFacade().findFirstByJpql(jpql, m);
        if (sec == null) {
            sec = new SolutionEvaluationSchema();
            sec.setCreatedAt(new Date());
            sec.setCreatedBy(webUserController.getLoggedUser());
            sec.setSolution(selected);
            sec.setEvaluationSchema(evaluationSchema);
            sec.setFrontEndDetail(true);
            getSesFacade().create(sec);
            JsfUtil.addSuccessMessage("Profile Successfully Created");
        } else {
            sec.setLastEditedAt(new Date());
            sec.setLastEditedBy(webUserController.getLoggedUser());
            sec.setEvaluationSchema(evaluationSchema);
            sec.setFrontEndDetail(true);
            getSesFacade().edit(sec);
        }
        solutionProfile = sec;
        comments = "";
        user = null;
        return toDetailProfileSolution();

    }

    public String toDetailProfileSolution() {
        if (solutionProfile == null) {
            JsfUtil.addErrorMessage("Nothing selected");
            return "";
        }
        selectedPoe = generateSolutionProfile(solutionProfile);
        return "/solution/detail_profile";
    }

    public void addNewSolutionItemToSolutionProfile() {
        if (solutionProfile == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return;
        }
        if (selectedPoe == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return;
        }
        if (newEi == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return;
        }
        for (Poeg poeg : selectedPoe.getPoegsList()) {
            for (PoEi poei : poeg.getPoeisList()) {
                if (poei.getEvaluationItem().equals(newEi)) {

                    SolutionItem si = new SolutionItem();
                    si.setSolutionEvaluationItem(poei.getSolutionEvaluationItem());
                    si.setCreatedAt(new Date());
                    si.setCreatedBy(webUserController.getLoggedUser());
                    siFacade.create(si);

                    PoItem poi = new PoItem();
                    poi.setSolutionItem(si);
                    poi.setPoei(poei);

                    poei.getPoItems().add(poi);

                }
            }
        }
    }

    public List<Solution> findP4pppCategorySolutions(P4PPPCategory cat) {
        List<Solution> sols = new ArrayList<>();
        String s;
        Map m = new HashMap();
        s = "select si.solutionEvaluationItem.solutionEvaluationGroup.solutionEvaluationScheme.solution "
                + " from SolutionItem si "
                + " where si.retired<>:ret "
                + " and  si.solutionEvaluationItem.solutionEvaluationGroup.solutionEvaluationScheme.frontEndDetail=:fd "
                + " and si.p4PPPCategory=:p4 "
                + " group by si.solutionEvaluationItem.solutionEvaluationGroup.solutionEvaluationScheme.solution";
        m.put("ret", true);
        m.put("p4", cat);
        m.put("fd", true);
        sols = getFacade().findByJpql(s, m);
        if (sols == null) {
            sols = new ArrayList<>();
        }
        return sols;
    }

    public String toViewSolutionProfile() {
        System.out.println("toViewSolutionProfile");
        viewingSolutionProfile = null;
        if (viewingSolution == null) {
            System.out.println("viewingSolution is null. return");
            return "";
        }
        String j;
        Map m = new HashMap();
        List<SolutionEvaluationSchema> sess;
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.solution=:sol "
                + " and se.frontEndDetail=:fed "
                + " order by se.id desc";

        m.put("sol", viewingSolution);
        m.put("fed", true);
        sess = getSesFacade().findByJpql(j, m);

        System.out.println("sess = " + sess);
        
        if (sess == null) {
            return "";
        }
        if (sess.isEmpty()) {
            return "";
        }
        System.out.println("sess = " + sess.size());
        if (sess.size() == 1) {
            viewingSolutionProfile = sess.get(0);
        } else {
            for (SolutionEvaluationSchema ses : sess) {
                if (ses.isFrontEndDefault()) {
                    viewingSolutionProfile = ses;
                }
            }
            if (viewingSolutionProfile == null) {
                viewingSolutionProfile = sess.get(0);
            }
        }
        System.out.println("viewingSolutionProfile = " + viewingSolutionProfile.getId());
        viewingPoe = generateSolutionProfile(viewingSolutionProfile);
        viewingDisplay = createDisplayFromPoe(viewingPoe);
        return "/profile";
    }

    public Display createDisplayFromPoe(Poe dpoe) {
        System.out.println("createDisplayFromPoe");
        System.out.println("dpoe = " + dpoe);
        Display d = new Display();
        if (dpoe == null) {
            return d;
        }
        int count = 0;
        for (Poeg poeg : dpoe.getPoegsList()) {
            for (PoEi pei : poeg.getPoeisList()) {
                if (pei.isParent()) {
                    for (PoEi sspei : pei.getSubEisList()) {
                        for (PoItem spoi : sspei.getPoItems()) {
                        }
                    }

                } else {
                    count++;
                    for (PoItem poi : pei.getPoItems()) {
                        if (poi.getSolutionItem() == null) {
                            continue;
                        }
                        if (poi.getSolutionItem().getSolutionEvaluationItem() == null) {
                            continue;
                        }
                        if (poi.getSolutionItem().getSolutionEvaluationItem().getEvaluationItem() == null) {
                            continue;
                        }

                        if (poi.getSolutionItem().getSolutionEvaluationItem().getEvaluationItem().getPlaceholder() == null) {
                            poi.getSolutionItem().getSolutionEvaluationItem().getEvaluationItem().setPlaceholder(Placeholder.General);
                        }

                        switch (poi.getSolutionItem().getSolutionEvaluationItem().getEvaluationItem().getPlaceholder()) {
                            case Functions:
                            case General:
                            case Implementation:
                            case Technology:
                            case Summary_Top:
                            case Summery_Bottom:
                            case Scoring:
                                DisplayItem tdi = new DisplayItem();
                                tdi.setDisplayItemType(DisplayItemType.h3);
                                tdi.setText(pei.getEvaluationItem().getName());
                                tdi.setOrderNo(count);
                                d.getDisplayItems(poi.getSolutionItem().getSolutionEvaluationItem().getEvaluationItem().getPlaceholder()).add(tdi);
                                break;
                        }

                        DisplayItem di = new DisplayItem();

                        if (poi.getSolutionItem().getSolutionEvaluationItem().getEvaluationItem().getDataType() == null) {
                            continue;
                        }

                        DataType tdt = poi.getSolutionItem().getSolutionEvaluationItem().getEvaluationItem().getDataType();
                        System.out.println("tdt = " + tdt);
                        System.out.println("poi.getSolutionItem() = " + poi.getSolutionItem().getId());
                        System.out.println("poi.getShortTextValue() = " + poi.getSolutionItem().getShortTextValue());
                        switch (tdt) {
                            case Short_Text:
                                di.setDisplayItemType(DisplayItemType.p);
                                di.setText(poi.getSolutionItem().getShortTextValue());
                                break;
                            case Long_Text:
                                di.setDisplayItemType(DisplayItemType.p);
                                di.setText(poi.getSolutionItem().getLongTextValue());
                                break;
                            case Item:
                                di.setDisplayItemType(DisplayItemType.p);
                                if (poi.getSolutionItem()!=null
                                        &&
                                        poi.getSolutionItem().getItemValue()!=null
                                        &&
                                        poi.getSolutionItem().getItemValue().getName() != null) {
                                    di.setText(poi.getSolutionItem().getItemValue().getName());
                                }
                                break;
                            case Long_Number:
                            case Real_Number:
                            case Integer_Number:break;
                        }
                        di.setDisplayItemType(DisplayItemType.p);
                        di.setOrderNo(count);
                        System.out.println("di = " + di.getText());
                        Placeholder tph = poi.getSolutionItem().getSolutionEvaluationItem().getEvaluationItem().getPlaceholder();
                        System.out.println("tph = " + tph);
                        List<DisplayItem> tdis = d.getDisplayItems(tph);
                        System.out.println("tdis = " + tdis);
                        tdis.add(di);
                        System.out.println("tdis = " + tdis);
                        
                    }

                }

            }

        }
        return d;
    }

    public void addNewSolutionParentItemToSolutionProfile() {
        if (solutionProfile == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return;
        }
        if (selectedPoe == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return;
        }
        if (newEi == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return;
        }

//        for (EvaluationItem ei : eis) {
//            SolutionEvaluationItem sei = findSolutionEvaluationItem(ei, seg);
//            sei.setOrderNo(onSei);
//
//            PoEi poei = new PoEi();
//            poei.setEvaluationItem(ei);
//            poei.setSolutionEvaluationItem(sei);
//
//            List<EvaluationItem> childrenOfEvaluationItem = findChildrenOfEvaluationItemForProfiling(ei);
//            if (childrenOfEvaluationItem != null && childrenOfEvaluationItem.size() > 0) {
//                double onCsei = 0.0;
//                for (EvaluationItem cei : childrenOfEvaluationItem) {
//
//                    SolutionEvaluationItem csei = findChildSolutionEvaluationItem(cei, sei);
//                    csei.setOrderNo(onCsei);
//
//                    List<SolutionItem> sis = findSolutionItems(csei);
//
//                    PoEi spoei = new PoEi();
//                    spoei.setEvaluationItem(cei);
//                    spoei.setSolutionEvaluationItem(csei);
//
//                    for (SolutionItem tsi : sis) {
//
//                        PoItem poi = new PoItem();
//                        poi.setSolutionItem(tsi);
//                        poi.setPoei(spoei);
//
//                        spoei.getPoItems().add(poi);
//
//                    }
//                    poei.getSubEis().put(cei.getId(), spoei);
//                    onCsei++;
//
//                }
//
//            } else {
//
//                List<SolutionItem> sis = findSolutionItems(sei);
//                for (SolutionItem tsi : sis) {
//                    PoItem poi = new PoItem();
//                    poi.setSolutionItem(tsi);
//                    poi.setPoei(poei);
//
//                    poei.getPoItems().add(poi);
//                }
//
//            }
//
//            poeg.getPoeis().put(ei.getId(), poei);
//            onSei++;
//
//        }
        for (Poeg poeg : selectedPoe.getPoegsList()) {
            for (PoEi poei : poeg.getPoeisList()) {
                if (poei.getEvaluationItem().equals(newEi)) {

                    SolutionItem si = new SolutionItem();
                    si.setSolutionEvaluationItem(poei.getSolutionEvaluationItem());
                    si.setCreatedAt(new Date());
                    si.setCreatedBy(webUserController.getLoggedUser());
                    siFacade.create(si);

                    PoItem poi = new PoItem();
                    poi.setSolutionItem(si);
                    poi.setPoei(poei);

                    poei.getPoItems().add(poi);

                }
            }
        }
    }

    public Poe generateSolutionProfile(SolutionEvaluationSchema soles) {
        System.out.println("generateSolutionProfile");
        if (soles == null) {
            JsfUtil.addErrorMessage("Nothing Selected");
            return null;
        }

        EvaluationSchema evalSch = soles.getEvaluationSchema();

        String j;
        Map m = new HashMap();

        Poe poe = new Poe();
        poe.setEvaluationSchema(soles.getEvaluationSchema());
        poe.setSolution(soles.getSolution());
        poe.setSolutionEvaluationSchema(soles);

        List<EvaluationGroup> egs = findEvaluationGroupsOfevaluationSchemaForProfiling(evalSch);

        double onSeg = 0.0;

        for (EvaluationGroup eg : egs) {

            SolutionEvaluationGroup seg = findSolutionEvaluationGroup(soles, eg);
            seg.setOrderNo(onSeg);

            Poeg poeg = new Poeg();
            poeg.setEvaluationGroup(eg);
            poeg.setSolutionEvaluationGroup(seg);

            double onSei = 0.0;
            List<EvaluationItem> eis = findRootEvalautionItemsOfEvaluationGroupForProfiling(eg);

            if (eis != null && !eis.isEmpty()) {

                for (EvaluationItem ei : eis) {
                    SolutionEvaluationItem sei = findSolutionEvaluationItem(ei, seg);
                    sei.setOrderNo(onSei);

                    PoEi poei = new PoEi();
                    poei.setEvaluationItem(ei);
                    poei.setSolutionEvaluationItem(sei);

                    List<EvaluationItem> childrenOfEvaluationItem = findChildrenOfEvaluationItemForProfiling(ei);
                    if (childrenOfEvaluationItem != null && childrenOfEvaluationItem.size() > 0) {
                        double onCsei = 0.0;
                        for (EvaluationItem cei : childrenOfEvaluationItem) {

                            SolutionEvaluationItem csei = findChildSolutionEvaluationItem(cei, sei);
                            csei.setOrderNo(onCsei);

                            List<SolutionItem> sis = findSolutionItems(csei);

                            PoEi spoei = new PoEi();
                            spoei.setEvaluationItem(cei);
                            spoei.setSolutionEvaluationItem(csei);

                            for (SolutionItem tsi : sis) {

                                PoItem poi = new PoItem();
                                poi.setSolutionItem(tsi);
                                poi.setPoei(spoei);

                                spoei.getPoItems().add(poi);

                            }
                            poei.getSubEis().put(cei.getId(), spoei);
                            onCsei++;

                        }

                    } else {

                        List<SolutionItem> sis = findSolutionItems(sei);
                        for (SolutionItem tsi : sis) {
                            PoItem poi = new PoItem();
                            poi.setSolutionItem(tsi);
                            poi.setPoei(poei);

                            poei.getPoItems().add(poi);
                        }

                    }

                    poeg.getPoeis().put(ei.getId(), poei);
                    onSei++;

                }
            }

            poe.getPoegs().put(eg.getId(), poeg);
            onSeg++;
        }
        sortPeo(poe);
        return poe;
    }

    public Poe sortPeo(Poe poe) {
        if (poe == null) {
            return null;
        }
        Collections.sort(poe.getPoegsList());
        for (Poeg g : poe.getPoegsList()) {
            Collections.sort(g.getPoeisList());
            for (PoEi ei : g.getPoeisList()) {
                Collections.sort(ei.getPoItems());
            }
        }
        return poe;
    }

    public String listEvaluationsToAccept() {
        assignData = true;
        acceptanceData = false;
        rejecData = false;
        completeData = false;
        enrollData = false;
        scoreData = false;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.solution=:sol "
                + " and se.evaluationSchema=:es"
                + " and se.assigned=:ass "
                + " and se.accepted=:acc "
                + " and se.rejected=:rej "
                + " order by se.id";
        m.put("ret", false);

        m.put("sol", selected);
        m.put("es", evaluationSchema);
        m.put("ass", true);
        m.put("acc", false);
        m.put("rej", false);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/consolidate_evaluations";
    }

    public String listEvaluationsRejected() {
        assignData = true;
        acceptanceData = false;
        rejecData = true;
        completeData = false;
        enrollData = false;
        scoreData = false;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.solution=:sol "
                + " and se.evaluationSchema=:es"
                + " and se.assigned=:ass "
                + " and se.accepted=:acc "
                + " and se.rejected=:rej "
                + " order by se.id";
        m.put("ret", false);

        m.put("sol", selected);
        m.put("es", evaluationSchema);
        m.put("ass", true);
        m.put("acc", false);
        m.put("rej", true);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/consolidate_evaluations";
    }

    public String listEvaluationsOngoing() {
        assignData = true;
        acceptanceData = true;
        rejecData = false;
        completeData = false;
        enrollData = false;
        scoreData = false;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.solution=:sol "
                + " and se.evaluationSchema=:es"
                + " and se.assigned=:ass "
                + " and se.accepted=:acc "
                + " and se.completed=:com "
                + " order by se.id";
        m.put("ret", false);

        m.put("sol", selected);
        m.put("es", evaluationSchema);
        m.put("ass", true);
        m.put("acc", false);
        m.put("com", false);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/consolidate_evaluations";
    }

    public String listEvaluationsCompleted() {
        assignData = true;
        acceptanceData = false;
        rejecData = false;
        completeData = true;
        enrollData = true;
        scoreData = true;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.solution=:sol "
                + " and se.evaluationSchema=:es"
                + " and se.completed=:com "
                + " order by se.id";
        m.put("ret", false);

        m.put("sol", selected);
        m.put("es", evaluationSchema);
        m.put("com", true);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/consolidate_evaluations";
    }

    public String listEvaluationsEnrolled() {
        assignData = true;
        acceptanceData = false;
        rejecData = false;
        completeData = true;
        enrollData = true;
        scoreData = true;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.solution=:sol "
                + " and se.evaluationSchema=:es"
                + " and se.enrolled=:enr "
                + " order by se.id";
        m.put("ret", false);

        m.put("sol", selected);
        m.put("es", evaluationSchema);
        m.put("enr", true);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/consolidate_evaluations";
    }

    public String listEvaluationsEnrolledReversed() {
        assignData = true;
        acceptanceData = false;
        rejecData = false;
        completeData = true;
        enrollData = true;
        scoreData = true;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.solution=:sol "
                + " and se.evaluationSchema=:es"
                + " and se.completed=:com "
                + " and se.enrolled=:en "
                + " and se.enrollRemoved=:enr "
                + " order by se.id";
        m.put("ret", false);

        m.put("sol", selected);
        m.put("es", evaluationSchema);
        m.put("com", true);
        m.put("en", false);
        m.put("enr", true);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/consolidate_evaluations";
    }

    public String listMyEvaluationsToAccept() {
        assignData = true;
        acceptanceData = false;
        rejecData = false;
        completeData = false;
        enrollData = false;
        scoreData = false;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.evaluationBy=:eb "
                + " and se.assigned=:ass "
                + " and se.accepted=:acc "
                + " and se.rejected=:rej "
                + " order by se.id";
        m.put("ret", false);
        m.put("eb", webUserController.getLoggedUser());
        m.put("ass", true);
        m.put("acc", false);
        m.put("rej", false);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/my_evaluations_to_accept";
    }

    public String listMyEvaluationsOngoing() {
        assignData = true;
        acceptanceData = true;
        rejecData = false;
        completeData = true;
        enrollData = false;
        scoreData = false;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.evaluationBy=:eb "
                + " and se.assigned=:ass "
                + " and se.accepted=:acc "
                + " and se.rejected=:rej "
                + " and se.completed=:com "
                + " order by se.id";
        m.put("ret", false);
        m.put("eb", webUserController.getLoggedUser());
        m.put("ass", true);
        m.put("acc", true);
        m.put("rej", false);
        m.put("com", false);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/my_evaluations_evaluating";
    }

    public String listMyEvaluationsCompleted() {
        assignData = true;
        acceptanceData = false;
        rejecData = false;
        completeData = false;
        enrollData = true;
        scoreData = true;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.evaluationBy=:eb "
                + " and se.assigned=:ass "
                + " and se.accepted=:acc "
                + " and se.completed=:com "
                + " order by se.id";
        m.put("ret", false);
        m.put("eb", webUserController.getLoggedUser());
        m.put("ass", true);
        m.put("acc", false);
        m.put("com", true);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/my_evaluations_completed";
    }

    public String listMyEvaluationsRejected() {
        assignData = true;
        acceptanceData = false;
        rejecData = true;
        completeData = false;
        enrollData = false;
        scoreData = false;
        String j;
        Map m = new HashMap();
        j = "select se "
                + " from SolutionEvaluationSchema se "
                + " where se.retired=:ret "
                + " and se.evaluationBy=:eb "
                + " and se.assigned=:ass "
                + " and se.rejected=:reg "
                + " order by se.id";
        m.put("ret", false);
        m.put("eb", webUserController.getLoggedUser());
        m.put("ass", true);
        m.put("reg", true);
        solutionEvaluationSchemas = getSesFacade().findByJpql(j, m);
        return "/solution/my_evaluations_rejected";
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
            items = new ArrayList<>();
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

    public Poe getSelectedPoe() {
        return selectedPoe;
    }

    public void setSelectedPoe(Poe selectedPoe) {
        this.selectedPoe = selectedPoe;
    }

    public SolutionItemFacade getSiFacade() {
        return siFacade;
    }

    public WebUser getUser() {
        return user;
    }

    public void setUser(WebUser user) {
        this.user = user;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isAssignData() {
        return assignData;
    }

    public void setAssignData(boolean assignData) {
        this.assignData = assignData;
    }

    public boolean isAcceptanceData() {
        return acceptanceData;
    }

    public void setAcceptanceData(boolean acceptanceData) {
        this.acceptanceData = acceptanceData;
    }

    public boolean isRejecData() {
        return rejecData;
    }

    public void setRejecData(boolean rejecData) {
        this.rejecData = rejecData;
    }

    public boolean isCompleteData() {
        return completeData;
    }

    public void setCompleteData(boolean completeData) {
        this.completeData = completeData;
    }

    public boolean isEnrollData() {
        return enrollData;
    }

    public void setEnrollData(boolean enrollData) {
        this.enrollData = enrollData;
    }

    public boolean isScoreData() {
        return scoreData;
    }

    public void setScoreData(boolean scoreData) {
        this.scoreData = scoreData;
    }

    public List<SolutionEvaluationSchema> getSolutionProfiles() {
        return solutionProfiles;
    }

    public void setSolutionProfiles(List<SolutionEvaluationSchema> solutionProfiles) {
        this.solutionProfiles = solutionProfiles;
    }

    public SolutionEvaluationSchema getSolutionProfile() {
        return solutionProfile;
    }

    public void setSolutionProfile(SolutionEvaluationSchema solutionProfile) {
        this.solutionProfile = solutionProfile;
    }

    public EvaluationItem getNewEi() {
        return newEi;
    }

    public void setNewEi(EvaluationItem newEi) {
        this.newEi = newEi;
    }

    public Solution getViewingSolution() {
        return viewingSolution;
    }

    public void setViewingSolution(Solution viewingSolution) {
        this.viewingSolution = viewingSolution;
    }

    public SolutionEvaluationSchema getViewingSolutionProfile() {
        return viewingSolutionProfile;
    }

    public void setViewingSolutionProfile(SolutionEvaluationSchema viewingSolutionProfile) {
        this.viewingSolutionProfile = viewingSolutionProfile;
    }

    public Poe getViewingPoe() {
        return viewingPoe;
    }

    public void setViewingPoe(Poe viewingPoe) {
        this.viewingPoe = viewingPoe;
    }

    public Display getViewingDisplay() {
        return viewingDisplay;
    }

    public void setViewingDisplay(Display viewingDisplay) {
        this.viewingDisplay = viewingDisplay;
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
