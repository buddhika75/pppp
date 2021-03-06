package cwcdh.pppp.bean;

import cwcdh.pppp.entity.Area;
import cwcdh.pppp.entity.WebUser;
import cwcdh.pppp.entity.Institution;
import cwcdh.pppp.entity.Item;
import cwcdh.pppp.entity.Person;
import cwcdh.pppp.entity.Upload;

import cwcdh.pppp.facade.InstitutionFacade;
import cwcdh.pppp.facade.EvaluationGroupFacade;
import cwcdh.pppp.facade.UploadFacade;
import cwcdh.pppp.facade.WebUserFacade;
import cwcdh.pppp.facade.util.JsfUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import cwcdh.pppp.entity.UserPrivilege;
import cwcdh.pppp.enums.EncounterType;
import cwcdh.pppp.enums.ImageType;
import cwcdh.pppp.enums.Privilege;
import cwcdh.pppp.enums.PrivilegeTreeNode;
import cwcdh.pppp.facade.UserPrivilegeFacade;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;

@Named
@SessionScoped
public class WebUserController implements Serializable {

    /*
    EJBs
     */
    @EJB
    private WebUserFacade ejbFacade;
    @EJB
    private InstitutionFacade institutionFacade;
    @EJB
    private UploadFacade uploadFacade;

    @EJB
    private EvaluationGroupFacade evaluationGroupFacadeFacade;
    @EJB
    private UserPrivilegeFacade userPrivilegeFacade;
    /*
    Controllers
     */
    @Inject
    private CommonController commonController;
    @Inject
    private AreaController areaController;
    @Inject
    private InstitutionController institutionController;
    @Inject
    private ItemController itemController;
    @Inject
    private SolutionController solutionController;
    @Inject
    private ImplementationController encounterController;
    @Inject
    MessageController messageController;
    @Inject
    UploadController uploadController;
    /*
    Variables
     */
    private List<WebUser> items = null;
    private List<Upload> currentProjectUploads;
    private List<Upload> clientUploads;
    private List<Upload> companyUploads;

    private List<Institution> loggableInstitutions;
    private List<Institution> loggablePmcis;
    private List<Area> loggableGnAreas;

    private Area selectedProvince;
    private Area selectedDistrict;
    private Area selectedDsArea;
    private Area selectedGnArea;
    private Institution selectedLocation;
    private Item selectedSourceOfFund;
    private Double selectedFundValue;
    private Item selectedFundUnit;
    private String selectedFundComments;

    private List<Area> districtsAvailableForSelection;

    private List<Area> selectedDsAreas;
    private List<Area> selectedGnAreas;
    private Area[] selectedProvinces;

    private WebUser current;
    private Upload currentUpload;
    private Institution institution;

    private WebUser loggedUser;
    private String userName;
    private String password;
    private String passwordReenter;
    private MapModel emptyModel;
    List<UserPrivilege> loggedUserPrivileges;

    private UploadedFile file;
    private String comments;

    private StreamedContent downloadingFile;

    private Date fromDate;
    private Date toDate;

    private Integer year;
    private Area province;
    private Area district;
    private Institution location;
    private Boolean allIslandProjects;
    private String searchKeyword;

    private String loginRequestResponse;

    private String locale;

    Long totalNumberOfRegisteredClients;
    private Long totalNumberOfClinicVisits;
    private Long totalNumberOfClinicEnrolments;

    /**
     *
     * Privileges
     *
     */
    private TreeNode allPrivilegeRoot;
    private TreeNode myPrivilegeRoot;
    private TreeNode[] selectedNodes;

    @PostConstruct
    public void init() {
        emptyModel = new DefaultMapModel();
    }

    public String toLogin() {
        return "/login";
    }
    
    public String toUploadImage(){
        if(current==null){
            JsfUtil.addErrorMessage("No user Selected");
            return "";
        }
        if(current.getImage()!=null){
            uploadController.setSelected(current.getImage());
        }else{
            Upload u = new Upload();
            u.setImageType(ImageType.User_Image);
            uploadController.setSelected(u);
            uploadController.save();
            current.setImage(u);
            save();
        }
        return "/webUser/user_image";
    }

    public String toRegister() {
        current = new WebUser();
        return "/webUser/register";
    }

    public List<Institution> findAutherizedInstitutions() {
        List<Institution> ins = new ArrayList<>();
        if (loggedUser == null) {
            return ins;
        }
        if (loggedUser.getInstitution() == null) {
            return ins;
        }
        ins.add(loggedUser.getInstitution());
        ins.addAll(institutionController.findChildrenInstitutions(loggedUser.getInstitution()));
        return ins;
    }

    public List<Institution> findAutherizedPmcis() {
        List<Institution> ins = new ArrayList<>();
        if (loggedUser == null) {
            return ins;
        }
        if (loggedUser.getInstitution() == null) {
            return ins;
        }
        if (loggedUser.getInstitution().isPmci()) {
            ins.add(loggedUser.getInstitution());
        }
        ins.addAll(institutionController.findChildrenPmcis(loggedUser.getInstitution()));
        return ins;
    }

    public String toManageInstitutionUsers() {
        String j = "select u from WebUser u "
                + " where u.retired=false "
                + " and u.institution in :inss ";
        Map m = new HashMap();
        m.put("inss", getLoggableInstitutions());
        items = getFacade().findByJpql(j, m);
        return "/insAdmin/manage_users";
    }

    public String toAddNewUserByInsAdmin() {
        current = new WebUser();
        password = "";
        passwordReenter = "";
        return "/insAdmin/create_new_user";
    }

    public String toManageAllUsers() {
        String j = "select u from WebUser u "
                + " where u.retired=false ";
        items = getFacade().findByJpql(j);
        return "/systemAdmin/manage_users";
    }

    public String toChangeMyDetails() {
        if (loggedUser == null) {
            return "";
        }
        current = loggedUser;
        return "/web_user/my_details";
    }

    public String toChangeMyPassword() {
        if (loggedUser == null) {
            return "";
        }
        password = "";
        passwordReenter = "";
        current = loggedUser;
        return "/webUser/my_password";
    }

    public String viewMedia() {
        if (currentUpload == null) {
            JsfUtil.addErrorMessage("Nothing is selected to view");
            return "";
        }
        if (currentUpload.getFileType().contains("image")) {
            return "/view_image";
        } else if (currentUpload.getFileType().contains("pdf")) {
            return "/view_pdf";
        } else {
            JsfUtil.addErrorMessage("NOT an image of a pdf file. ");
            return "";
        }
    }

    public String toSubmitClientRequest() {
        return "/finalize_client_request";
    }

    public void sendSubmitClientRequestConfirmationEmail() {

    }

    public void downloadCurrentFile() {
        if (currentUpload == null) {
            return;
        }
        InputStream stream = new ByteArrayInputStream(currentUpload.getBaImage());
        downloadingFile = new DefaultStreamedContent(stream, currentUpload.getFileType(), currentUpload.getFileName());
    }

    public StreamedContent getDownloadingFile() {
        downloadCurrentFile();
        return downloadingFile;
    }

    public String prepareRegisterAsClient() {
        current = new WebUser();

        currentProjectUploads = null;
        companyUploads = null;
        clientUploads = null;
        currentUpload = null;

        return "/register";
    }

    public String registerUser() {
        if (!current.getWebUserPassword().equals(password)) {
            JsfUtil.addErrorMessage("Passwords are not matching. Please retry.");
            return "";
        }

        try {
            getFacade().create(current);
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Username already taken. Please enter a different username");
            return "";
        }

        setLoggedUser(current);
        JsfUtil.addSuccessMessage("Your Details Added as an institution user. Please contact us for changes");
        return "/index_1";
    }

    public String logOut() {
        loggedUser = null;
        return "/index";
    }

    public String login() {
        return login(false);
    }

    public String login(boolean withoutPassword) {
        loggableInstitutions = null;
        loggablePmcis = null;
        loggableGnAreas = null;
        institutionController.setMyClinics(null);
        if (userName == null || userName.trim().equals("")) {
            JsfUtil.addErrorMessage("Please enter a Username");
            return "";
        }
        if (!withoutPassword) {
            if (password == null || password.trim().equals("")) {
                JsfUtil.addErrorMessage("Please enter the Password");
                return "";
            }
        }
        if (!isFirstVisit()) {
            if (!checkLogin(withoutPassword)) {
                JsfUtil.addErrorMessage("Username/Password Error. Please retry.");
                return "";
            }
        }

        JsfUtil.addSuccessMessage("Successfully Logged");
        return "/index_1";
    }

    public String toHome() {
        return "/index";
    }

    public String toComponents() {
        return "/components";
    }

    public String toAbout() {
        return "/about";
    }

 

    public String loginForMobile() {
        loginRequestResponse = "";
        if (userName == null || userName.trim().equals("")) {
            loginRequestResponse += "Wrong Isername. Please go back to settings and update.";
            return "/mobile/login_failure";
        }
        if (password == null || password.trim().equals("")) {
            loginRequestResponse += "Wrong Isername. Please go back to settings and update.";
            return "/mobile/login_failure";
        }
        if (!checkLogin(false)) {
            loginRequestResponse += "Wrong Isername. Please go back to settings and update.";
            return "/mobile/login_failure";
        }
        return "/mobile/index";
    }

    public List<WebUser> completeUsers(String qry) {
        String temSQL;
        temSQL = "SELECT u FROM WebUser u WHERE lower(u.name) like :userName and u.retired =:ret";
        Map m = new HashMap();
        m.put("userName", "%" + qry.trim().toLowerCase() + "%");
        m.put("ret", false);
        return getFacade().findByJpql(temSQL, m);
    }

    public List<WebUser> completeUsersByName(String qry) {
        String temSQL;
        temSQL = "SELECT u "
                + " FROM WebUser u "
                + " WHERE lower(u.person.name) like :qry "
                + " and u.retired =:ret "
                + " order by u.person.name";
        Map m = new HashMap();
        m.put("qry", "%" + qry.trim().toLowerCase() + "%");
        m.put("ret", false);
        return getFacade().findByJpql(temSQL, m);
    }

    private boolean checkLogin(boolean withoutPassword) {
        if (loggedUser != null && withoutPassword) {
            return true;
        }

        if (getFacade() == null) {
            JsfUtil.addErrorMessage("Server Error");
            return false;
        }

        String temSQL;
        temSQL = "SELECT u FROM WebUser u WHERE lower(u.name)=:userName and u.retired =:ret";
        Map m = new HashMap();
        m.put("userName", userName.trim().toLowerCase());
        m.put("ret", false);
        loggedUser = getFacade().findFirstByJpql(temSQL, m);
        if (loggedUser == null) {
            return false;
        }
        if (withoutPassword) {
            return true;
        }
        if (commonController.matchPassword(password, loggedUser.getWebUserPassword())) {
            return true;
        } else {
            loggedUser = null;
            return false;
        }

    }

    private boolean isFirstVisit() {
        if (getFacade() == null) {
            JsfUtil.addErrorMessage("Server Config Error.");
            return false;
        }
        String j = "select c from WebUser c";
        WebUser w = getFacade().findFirstByJpql(j);
        if (w == null) {
            JsfUtil.addSuccessMessage("First Visit");

            Institution ins = new Institution();
            ins.setName("Commonwealth Centre for Digital Health");
            getInstitutionFacade().create(ins);
            WebUser wu = new WebUser();
            wu.getPerson().setName(userName);
            wu.setName(userName);
            String tp = commonController.hash(password);
            wu.setWebUserPassword(tp);
            wu.setInstitution(ins);

            getFacade().create(wu);
            loggedUser = wu;

            return true;
        } else {
            return false;
        }

    }

    public WebUserController() {
    }

    public WebUser getSelected() {
        return current;
    }

    private WebUserFacade getFacade() {
        return ejbFacade;
    }

    public String prepareList() {
        recreateModel();
        return "manage_users";
    }

    public String prepareView() {
        return "/webUser/View";
    }

    public String toCreateNewUserBySysAdmin() {
        current = new WebUser();
        password = "";
        passwordReenter = "";
        return "/webUser/create_new_user";
    }

    public String create() {
        if (!password.equals(passwordReenter)) {
            JsfUtil.addErrorMessage("Passwords do NOT match");
            return "";
        }
        try {
            current.setWebUserPassword(commonController.hash(password));
            current.setCreatedAt(new Date());
            current.setCreater(loggedUser);
            getFacade().create(current);
            JsfUtil.addSuccessMessage(("A new User Created Successfully."));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ("Error Occured. Please change username and try again."));
            return "";
        }
        return "index";
    }
    
    public void save(){
        save(current);
    }
    
    public void save(WebUser u){
        if(u==null){
            return ;
            
        }
        if(u.getId()==null){
            if(u.getCreatedAt()==null){
                u.setCreatedAt(new Date());
            }
            if(u.getCreater()==null){
                u.setCreater(loggedUser);
            }
            getFacade().create(u);
        }else{
            if(u.getLastEditBy()==null){
                u.setLastEditBy(loggedUser);
            }
            if(u.getLastEditeAt()==null){
                u.setLastEditeAt(new Date());
            }
            getFacade().edit(u);
        }
    }

    public String saveNewWebUserByInsAdmin() {
        if (current == null) {
            JsfUtil.addErrorMessage("Noting to save");
            return "";
        }
        if (!password.equals(passwordReenter)) {
            JsfUtil.addErrorMessage("Passwords do NOT match");
            return "";
        }
        if (userNameExsists(current.getName())) {
            JsfUtil.addErrorMessage("Username already exists. Please try another.");
            return "";
        }
        if (current.getId() != null) {
            current.setLastEditBy(loggedUser);
            current.setLastEditeAt(new Date());
            getFacade().edit(current);
            JsfUtil.addSuccessMessage("User Details Updated");
            return "";
        }
        try {
            current.setWebUserPassword(commonController.hash(password));
            current.setCreatedAt(new Date());
            current.setCreater(loggedUser);
            getFacade().create(current);
            JsfUtil.addSuccessMessage(("A new User Created Successfully."));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ("Error Occured. Please change username and try again."));
            return "";
        }
        return "/insAdmin/user_index";
    }

    public boolean userNameExsists() {
        if (getSelected() == null) {
            return false;
        }

        boolean une = userNameExsists(getSelected().getName());
        return une;
    }

    public boolean userNameExsists(String un) {
        if (un == null) {
            return false;
        }
        String j = "select u from WebUser u where lower(u.name)=:un order by u.id desc";
        Map m = new HashMap();
        m.put("un", un.toLowerCase());
        WebUser u = getFacade().findFirstByJpql(j, m);
        return u != null;
    }

    public String selfRegisterNewUser() {
        if (getSelected() == null) {
            JsfUtil.addErrorMessage("Noting to save");
            return "";
        }

        if (!password.equals(passwordReenter)) {
            JsfUtil.addErrorMessage("Passwords do NOT match");
            return "";
        }
        if (userNameExsists(getSelected().getName())) {
            JsfUtil.addErrorMessage("Email already exists. Please try another.");
            return "";
        } else {
            getSelected().setEmail(getSelected().getName());
        }
        if (getSelected().getId() != null) {
            getSelected().setLastEditBy(loggedUser);
            getSelected().setLastEditeAt(new Date());
            getFacade().edit(getSelected());
        } else {
            try {
                current.setWebUserPassword(commonController.hash(password));
                current.setCreatedAt(new Date());
                current.setCreater(loggedUser);
                getFacade().create(current);
                JsfUtil.addSuccessMessage(("A new User Created Successfully."));
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, ("Error Occured. Please change the email and try again."));
                return "";
            }
        }
        UserPrivilege up = new UserPrivilege();
        up.setPrivilege(Privilege.Unregistered_User);
        up.setWebUser(current);
        getUserPrivilegeFacade().create(up);
        return "/webUser/confirm_email";
    }

    public void sendConfirmationEmail() {

    }

    public String saveNewWebUserBySysAdmin() {
        if (getSelected() == null) {
            JsfUtil.addErrorMessage("Noting to save");
            return "";
        }

        if (!password.equals(passwordReenter)) {
            JsfUtil.addErrorMessage("Passwords do NOT match");
            return "";
        }
        if (userNameExsists(getSelected().getName())) {
            JsfUtil.addErrorMessage("Username already exists. Please try another.");
            return "";
        }
        if (getSelected().getId() != null) {
            getSelected().setLastEditBy(loggedUser);
            getSelected().setLastEditeAt(new Date());
            getFacade().edit(getSelected());
            JsfUtil.addSuccessMessage("User Details Updated");
            return "/webUser/index";
        }
        try {
            current.setWebUserPassword(commonController.hash(password));
            current.setCreatedAt(new Date());
            current.setCreater(loggedUser);
            getFacade().create(current);
            JsfUtil.addSuccessMessage(("A new User Created Successfully."));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ("Error Occured. Please change username and try again."));
            return "";
        }
        return "/webUser/index";
    }

    public String prepareEdit() {
        return "Edit";
    }
    
    public String prepareEditPassword() {
        return "Password";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(("Updated"));
            return "manage_users";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }

    public String updateMyDetails() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(("Your details Updated."));
            return "/index_1";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, e.getMessage());
            return null;
        }
    }

    public String updateMyPassword() {
        current = loggedUser;
        if (current == null) {
            JsfUtil.addSuccessMessage(("Error. No Logged User"));
            return "";
        }

        if (!password.equals(passwordReenter)) {
            JsfUtil.addSuccessMessage(("Password Mismatch."));
            return "";
        }
        current.setWebUserPassword(commonController.hash(password));
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(("Password Updated"));
            password = "";
            passwordReenter = "";
            return "/index_1";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, e.getMessage());
            return "";
        }
    }

    public void updateLoggedUser() {
        if (loggedUser == null) {
            return;
        }
        try {
            getFacade().edit(loggedUser);
            JsfUtil.addSuccessMessage(("Updated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, e.getMessage());
        }
    }

    public String updatePassword() {
        if (!password.equals(passwordReenter)) {
            JsfUtil.addErrorMessage("Passwords do NOT match.");
            return "";
        }
        try {
            String hashedPassword = commonController.hash(password);
            current.setWebUserPassword(hashedPassword);
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(("Password Changed."));
            return "index";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        performDestroy();
        recreateModel();
        return "manage_users";
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(("WebUserDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ("PersistenceErrorOccured"));
        }
    }

    public List<WebUser> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public WebUser getWebUser(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

    public WebUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(WebUser loggedUser) {
        this.loggedUser = loggedUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public cwcdh.pppp.facade.WebUserFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(cwcdh.pppp.facade.WebUserFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public WebUser getCurrent() {
        return current;
    }

    public void setCurrent(WebUser current) {
        this.current = current;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public MapModel getEmptyModel() {
        return emptyModel;
    }

    public void setEmptyModel(MapModel emptyModel) {
        this.emptyModel = emptyModel;
    }

    public UploadFacade getUploadFacade() {
        return uploadFacade;
    }

    public void setUploadFacade(UploadFacade uploadFacade) {
        this.uploadFacade = uploadFacade;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Upload getCurrentUpload() {
        return currentUpload;
    }

    public void setCurrentUpload(Upload currentUpload) {
        this.currentUpload = currentUpload;
    }

    public void setCurrentProjectUploads(List<Upload> currentProjectUploads) {
        this.currentProjectUploads = currentProjectUploads;
    }

    public Date getFromDate() {
        if (fromDate == null) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DATE, 1);
            fromDate = c.getTime();
        }
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        if (toDate == null) {
            toDate = new Date();
        }
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void setClientUploads(List<Upload> clientUploads) {
        this.clientUploads = clientUploads;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Area[] getSelectedProvinces() {
        return selectedProvinces;
    }

    public void setSelectedProvinces(Area[] selectedProvinces) {
        this.selectedProvinces = selectedProvinces;
    }

    public void setSelectedDsAreas(List<Area> selectedDsAreas) {
        this.selectedDsAreas = selectedDsAreas;
    }

    public List<Area> getSelectedGnAreas() {
        return selectedGnAreas;
    }

    public void setSelectedGnAreas(List<Area> selectedGnAreas) {
        if (selectedGnAreas == null) {
            selectedGnAreas = new ArrayList<>();
        }
        this.selectedGnAreas = selectedGnAreas;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Area getProvince() {
        return province;
    }

    public void setProvince(Area province) {
        this.province = province;
    }

    public Area getDistrict() {
        return district;
    }

    public void setDistrict(Area district) {
        this.district = district;
    }

    public Institution getLocation() {
        return location;
    }

    public void setLocation(Institution location) {
        this.location = location;
    }

    public Boolean getAllIslandProjects() {
        return allIslandProjects;
    }

    public void setAllIslandProjects(Boolean allIslandProjects) {
        this.allIslandProjects = allIslandProjects;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getLoginRequestResponse() {
        return loginRequestResponse;
    }

    public void setLoginRequestResponse(String loginRequestResponse) {
        this.loginRequestResponse = loginRequestResponse;
    }

    public String getLocale() {
        if (loggedUser != null) {
            locale = loggedUser.getDefLocale();
        }
        if (locale == null || locale.trim().equals("")) {
            locale = "en";
        }
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public AreaController getAreaController() {
        return areaController;
    }

    public InstitutionController getInstitutionController() {
        return institutionController;
    }

    public ItemController getItemController() {
        return itemController;
    }

    public String getPasswordReenter() {
        return passwordReenter;
    }

    public void setPasswordReenter(String passwordReenter) {
        this.passwordReenter = passwordReenter;
    }

    public Area getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(Area selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

    public Area getSelectedDistrict() {
        return selectedDistrict;
    }

    public void setSelectedDistrict(Area selectedDistrict) {
        this.selectedDistrict = selectedDistrict;
    }

    public Area getSelectedDsArea() {
        return selectedDsArea;
    }

    public void setSelectedDsArea(Area selectedDsArea) {
        this.selectedDsArea = selectedDsArea;
    }

    public Area getSelectedGnArea() {
        return selectedGnArea;
    }

    public void setSelectedGnArea(Area selectedGnArea) {
        this.selectedGnArea = selectedGnArea;
    }

    public Institution getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(Institution selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public Item getSelectedSourceOfFund() {
        return selectedSourceOfFund;
    }

    public void setSelectedSourceOfFund(Item selectedSourceOfFund) {
        this.selectedSourceOfFund = selectedSourceOfFund;
    }

    public Double getSelectedFundValue() {
        return selectedFundValue;
    }

    public void setSelectedFundValue(Double selectedFundValue) {
        this.selectedFundValue = selectedFundValue;
    }

    public Item getSelectedFundUnit() {
        return selectedFundUnit;
    }

    public void setSelectedFundUnit(Item selectedFundUnit) {
        this.selectedFundUnit = selectedFundUnit;
    }

    public String getSelectedFundComments() {
        return selectedFundComments;
    }

    public void setSelectedFundComments(String selectedFundComments) {
        this.selectedFundComments = selectedFundComments;
    }

    public EvaluationGroupFacade getEvaluationGroupFacadeFacade() {
        return evaluationGroupFacadeFacade;
    }

    public TreeNode getAllPrivilegeRoot() {
        return allPrivilegeRoot;
    }

    public void setAllPrivilegeRoot(TreeNode allPrivilegeRoot) {
        this.allPrivilegeRoot = allPrivilegeRoot;
    }

    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public TreeNode getMyPrivilegeRoot() {
        return myPrivilegeRoot;
    }

    public Long getTotalNumberOfRegisteredClients() {
        return totalNumberOfRegisteredClients;
    }

    public void setTotalNumberOfRegisteredClients(Long totalNumberOfRegisteredClients) {
        this.totalNumberOfRegisteredClients = totalNumberOfRegisteredClients;
    }

    public void setMyPrivilegeRoot(TreeNode myPrivilegeRoot) {
        this.myPrivilegeRoot = myPrivilegeRoot;
    }

    public UserPrivilegeFacade getUserPrivilegeFacade() {
        return userPrivilegeFacade;
    }

    public List<Upload> getCompanyUploads() {
        return companyUploads;
    }

    public void setCompanyUploads(List<Upload> companyUploads) {
        this.companyUploads = companyUploads;
    }

    public List<Area> getDistrictsAvailableForSelection() {
        return districtsAvailableForSelection;
    }

    public void setDistrictsAvailableForSelection(List<Area> districtsAvailableForSelection) {
        this.districtsAvailableForSelection = districtsAvailableForSelection;
    }

    public List<UserPrivilege> getLoggedUserPrivileges() {
        return loggedUserPrivileges;
    }

    public void setLoggedUserPrivileges(List<UserPrivilege> loggedUserPrivileges) {
        this.loggedUserPrivileges = loggedUserPrivileges;
    }

    public List<Institution> getLoggableInstitutions() {
        if (loggableInstitutions == null) {
            loggableInstitutions = findAutherizedInstitutions();
        }
        return loggableInstitutions;
    }

    public void setLoggableInstitutions(List<Institution> loggableInstitutions) {
        this.loggableInstitutions = loggableInstitutions;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<Institution> getLoggablePmcis() {
        if (loggablePmcis == null) {
            loggablePmcis = findAutherizedPmcis();
        }
        return loggablePmcis;
    }

    public void setLoggablePmcis(List<Institution> loggablePmcis) {
        this.loggablePmcis = loggablePmcis;
    }

    public Long getTotalNumberOfClinicVisits() {
        return totalNumberOfClinicVisits;
    }

    public void setTotalNumberOfClinicVisits(Long totalNumberOfClinicVisits) {
        this.totalNumberOfClinicVisits = totalNumberOfClinicVisits;
    }

    public SolutionController getsolutionController() {
        return solutionController;
    }

    public ImplementationController getEncounterController() {
        return encounterController;
    }

    public Long getTotalNumberOfClinicEnrolments() {
        return totalNumberOfClinicEnrolments;
    }

    public void setTotalNumberOfClinicEnrolments(Long totalNumberOfClinicEnrolments) {
        this.totalNumberOfClinicEnrolments = totalNumberOfClinicEnrolments;
    }

    @FacesConverter(forClass = WebUser.class)
    public static class WebUserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            WebUserController controller = (WebUserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "webUserController");
            return controller.getWebUser(getKey(value));
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
            if (object instanceof WebUser) {
                WebUser o = (WebUser) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + WebUser.class.getName());
            }
        }

    }

}
