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
import cwcdh.pppp.enums.DataType;
import cwcdh.pppp.enums.MultipleItemCalculationMethod;
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
    private SolutionEvaluationSchema solutionEvaluationSchema;
    private EvaluationSchema evaluationSchema;
    private Poe selectedPoe;

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

    public SolutionEvaluationGroup findSolutionEvaluationGroup(SolutionEvaluationSchema solutionEvaluationSchema, EvaluationGroup evaluationGroup) {
        System.out.println("findSolutionEvaluationGroup");
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
        System.out.println("m = " + m);
        System.out.println("j = " + j);
        seg = getSegFacade().findFirstByJpql(j, m);
        System.out.println("seg = " + seg);
        if (seg == null) {
            seg = new SolutionEvaluationGroup();
            seg.setEvaluationGroup(evaluationGroup);
            seg.setSolutionEvaluationScheme(solutionEvaluationSchema);
            seg.setCreatedAt(new Date());
            seg.setCreatedBy(webUserController.getLoggedUser());
            System.out.println("webUserController.getLoggedUser() = " + webUserController.getLoggedUser());
            getSegFacade().create(seg);
            System.out.println("new Seg = " + seg);
        }
        return seg;
    }

    public SolutionEvaluationItem findSolutionEvaluationItem(EvaluationItem evaluationItem, SolutionEvaluationGroup solutionEvaluationGroup) {
        String j;
        Map m = new HashMap();
        j = "select sei from SolutionEvaluationItem sei "
                + " where sei.retired=:ret "
                + " and sei.evaluationItem=:ei "
                + " and sei.solutionEvaluationGroup=:seg";
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

    public double findSolutionEvaluationItemScore(PoEi poei) {
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
            System.out.println("poeg.getEvaluationGroup().getName() = " + poeg.getEvaluationGroup().getName());
            solutionGroupScore = 0.0;

            for (PoEi poei : poeg.getPoeisList()) {
                solutionRootElementScore = 0.0;
                if (poei.getSubEisList().isEmpty()) {
                    solutionRootElementScore = findSolutionEvaluationItemScore(poei);
                    System.out.println("solutionRootElementScore = " + solutionRootElementScore);
                } else {
                    System.out.println("Still to calculate");
                }

                double temWeigtage= poei.getEvaluationItem().getWeight();
                
                solutionGroupScore += (solutionRootElementScore*temWeigtage);
                
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

    public Poe getSelectedPoe() {
        return selectedPoe;
    }

    public void setSelectedPoe(Poe selectedPoe) {
        this.selectedPoe = selectedPoe;
    }

    public SolutionItemFacade getSiFacade() {
        return siFacade;
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
