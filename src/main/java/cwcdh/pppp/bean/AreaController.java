package cwcdh.pppp.bean;

import cwcdh.pppp.entity.Area;
import cwcdh.pppp.facade.AreaFacade;
import cwcdh.pppp.facade.util.JsfUtil;
import cwcdh.pppp.facade.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import cwcdh.pppp.entity.Item;
import org.primefaces.model.UploadedFile;

@Named
@SessionScoped
public class AreaController implements Serializable {

    @EJB
    private AreaFacade ejbFacade;

    private List<Area> items = null;
    private Area selected;
    private Area deleting;
    private UploadedFile file;

    @Inject
    private WebUserController webUserController;
    @Inject
    private CommonController commonController;
    
   



    public String toAddArea() {
        selected = new Area();
        return "/area/area";
    }

    public String toEditAreaForSysAdmin() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Please select an Area to Edit");
            return "";
        }
        return "/area/area";
    }

    public String deleteAreaForSysAdmin() {
        if (deleting == null) {
            JsfUtil.addErrorMessage("Please select an Area to Delete");
            return "";
        }
        deleting.setRetired(true);
        deleting.setRetiredAt(new Date());
        deleting.setRetiredBy(webUserController.getLoggedUser());
        getFacade().edit(deleting);
        items = null;
        deleting = null;
        return toListAreasForSysAdmin();
    }

    public String saveOrUpdateAreaForSystemAdmin() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Please select an Area");
            return "";
        }
        if (selected.getId() == null) {
            selected.setCreatedAt(new Date());
            selected.setCreatedBy(webUserController.getLoggedUser());
            getFacade().create(selected);
            JsfUtil.addSuccessMessage("Saved");
        } else {
            selected.setLastEditBy(webUserController.getLoggedUser());
            selected.setLastEditeAt(new Date());
            getFacade().edit(deleting);
            JsfUtil.addSuccessMessage("Updated");
        }
        items = null;
        selected = null;
        return toListAreasForSysAdmin();
    }

    public String toListAreasForSysAdmin() {
        String j = "select a from Area a where a.retired=:ret order by a.name";
        Map m = new HashMap();
        m.put("ret", false);
        items = getFacade().findByJpql(j, m);
        return "/area/list";
    }

    public String toBrowseAreasForSysAdmin() {

        return "/area/browse";
    }

    public String toSearchAreasForSysAdmin() {

        return "/area/search";
    }

   

    public Area getAreaById(Long id) {
        return getFacade().find(id);
    }

   

    public List<Area> getAreas(Item  areaType, Area superArea) {
        return getAreas(areaType, superArea, null);
    }

    public List<Area> getAreas(Item areaType, Area parentArea, Area grandParentArea) {
        return getAreas(areaType, parentArea, grandParentArea, null);
    }

    public List<Area> getAreas(Item areaType, Area parentArea, Area grandParentArea, String qry) {
        String j;
        Map m = new HashMap();
        j = "select a "
                + " from Area a "
                + " where a.name is not null ";
        if (areaType != null) {
            j += " and a.type=:t";
            m.put("t", areaType);
        }
        if (parentArea != null) {
            j += " and a.parentArea=:pa ";
            m.put("pa", parentArea);
        }
        if (grandParentArea != null) {
            j += " and a.parentArea.parentArea=:gpa ";
            m.put("gpa", grandParentArea);
        }
        if (qry != null) {
            j += " and lower(a.name) like :qry ";
            m.put("qry", "%" + qry.toLowerCase() + "%");
        }
        j += " order by a.name";
        // //System.out.prIntegerln("m = " + m);
        List<Area> areas = getFacade().findByJpql(j, m);
        return areas;
    }



    public List<Area> getAreas(String qry, Item areaType) {
        String j;
        Map m = new HashMap();
        j = "select a "
                + " from Area a "
                + " where upper(a.name) like :n   ";
        m.put("n", "%" + qry.toUpperCase() + "%");
        if (areaType != null) {
            j += " and a.type=:t";
            m.put("t", areaType);
        }
        j += " order by a.code";
        return getFacade().findByJpql(j, m);
    }

    public Area getAreaByCode(String code, Item areaType) {
        if (code.trim().equals("")) {
            return null;
        }
        String j;
        Map m = new HashMap();
        j = "select a "
                + " from Area a "
                + " where a.retired=:ret "
                + " and upper(a.code)=:n  ";
        m.put("n", code.toUpperCase());
        m.put("ret", false);
        if (areaType != null) {
            j += " and a.type=:t";
            m.put("t", areaType);
        }
        j += " order by a.id desc";
        Area ta = getFacade().findFirstByJpql(j, m);
        return ta;
    }

    public Area getAreaByUid(Long code, Item areaType) {
        if (code == null) {
            return null;
        }
        String j;
        Map m = new HashMap();
        j = "select a "
                + " from Area a "
                + " where a.retired=:ret "
                + " and a.areauid=:n  ";
        m.put("n", code);
        m.put("ret", false);
        if (areaType != null) {
            j += " and a.type=:t";
            m.put("t", areaType);
        }
        j += " order by a.id desc";
        Area ta = getFacade().findFirstByJpql(j, m);
        return ta;
    }

    public Area getAreaByName(String nameOrCode, Item areaType, boolean createNew, Area parentArea) {
        if (nameOrCode.trim().equals("")) {
            return null;
        }
        String j;
        Map m = new HashMap();
        j = "select a "
                + " from Area a "
                + " where upper(a.name)=:n  ";
        m.put("n", nameOrCode.toUpperCase());
        if (areaType != null) {
            j += " and a.type=:t";
            m.put("t", areaType);
        }
        j += " order by a.code";
//        // //System.out.prIntegerln("m = " + m);
        Area ta = getFacade().findFirstByJpql(j, m);
        if (ta == null && createNew) {
            ta = new Area();
            ta.setName(nameOrCode);
            ta.setType(areaType);
            ta.setCreatedAt(new Date());
            ta.setCreatedBy(webUserController.getLoggedUser());
            ta.setParentArea(parentArea);
            getFacade().create(ta);
        }
        return ta;
    }

    public Area getAreaByCodeAndName(String code, String name, Item areaType, boolean createNew, Area parentArea) {
        try {
            if (code.trim().equals("")) {
                return null;
            }
            String j;
            Map m = new HashMap();
            j = "select a "
                    + " from Area a "
                    + " where upper(a.name) =:n and upper(a.code) =:c ";
            m.put("c", code.toUpperCase());
            m.put("n", name.toUpperCase());
            if (areaType != null) {
                j += " and a.type=:t";
                m.put("t", areaType);
            }
            j += " order by a.code";
//            // //System.out.prIntegerln("m = " + m);
            Area ta = getFacade().findFirstByJpql(j, m);
            if (ta == null && createNew) {
                ta = new Area();
                ta.setCode(code);
                ta.setType(areaType);
                ta.setCreatedAt(new Date());
                ta.setCreatedBy(webUserController.getLoggedUser());
                ta.setParentArea(parentArea);
                getFacade().create(ta);
            }
            return ta;
        } catch (Exception e) {
            // //System.out.prIntegerln("e = " + e);
            // //System.out.prIntegerln("code = " + code);
            return null;
        }
    }

    public AreaController() {
    }

    public Area getSelected() {
        return selected;
    }

    public void setSelected(Area selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AreaFacade getFacade() {
        return ejbFacade;
    }

    public Area prepareCreate() {
        selected = new Area();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, "Created");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, "Updated");
    }

    public void destroy() {
        persist(PersistAction.DELETE, "Deleted");
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Area> getItems() {
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
                    JsfUtil.addErrorMessage(ex, "Error");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "Error");
            }
        }
    }

    public List<Area> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Area> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

   

    
    public WebUserController getWebUserController() {
        return webUserController;
    }

   

    public AreaFacade getEjbFacade() {
        return ejbFacade;
    }

   

    public CommonController getCommonController() {
        return commonController;
    }

  

   

    public Area getDeleting() {
        return deleting;
    }

    public void setDeleting(Area deleting) {
        this.deleting = deleting;
    }

   

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Converters">
    @FacesConverter(forClass = Area.class)
    public static class AreaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AreaController controller = (AreaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "areaController");
            return controller.getFacade().find(getKey(value));
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
            if (object instanceof Area) {
                Area o = (Area) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Area.class.getName()});
                return null;
            }
        }

    }

    @FacesConverter(value = "areaConverter")
    public static class AreaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AreaController controller = (AreaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "areaController");
            return controller.getFacade().find(getKey(value));
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
            if (object instanceof Area) {
                Area o = (Area) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Area.class.getName()});
                return null;
            }
        }

    }

    // </editor-fold>
}
