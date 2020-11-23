package cwcdh.pppp.bean;

import cwcdh.pppp.entity.Upload;
import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.bean.util.JsfUtil.PersistAction;
import cwcdh.pppp.entity.Solution;
import cwcdh.pppp.enums.ImageType;
import cwcdh.pppp.enums.MessageType;
import cwcdh.pppp.facade.MessageFacade;
import cwcdh.pppp.facade.SolutionFacade;
import cwcdh.pppp.facade.UploadFacade;
import cwcdh.pppp.facade.WebUserFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
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
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;

@Named
@SessionScoped
public class UploadController implements Serializable {

    @EJB
    private UploadFacade ejbFacade;
    @EJB
    SolutionFacade solutionFacade;
    @EJB
    MessageFacade messageFacade;
    @EJB
    WebUserFacade webUserFacade;

    @Inject
    private WebUserController webUserController;
    @Inject
    private ItemController itemController;

    private List<Upload> items = null;

    private List<Upload> productImages = null;

    private Upload selected;
    private UploadedFile file;
    private Solution solution;

    @Inject
    MessageController messageController;

    public UploadController() {
    }

    public Upload getSelected() {
        return selected;
    }

    public void setSelected(Upload selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public String toListUploads() {
        String j = "select u from Upload u "
                + " where u.retired<>:ret";
        Map m = new HashMap();
        m.put("ret", true);
        items = getFacade().findByJpql(j, m);
        return "/upload/upload_list";
    }

    public String toUploadsNewSiteImage() {
        selected = new Upload();
        selected.setCreatedAt(new Date());
        selected.setCreater(webUserController.getLoggedUser());
        ImageType imageType = ImageType.Profile;
        selected.setImageType(imageType);
        return "/upload/upload";
    }

    public String toUploadsNewProductImage() {
        selected = new Upload();
        selected.setCreatedAt(new Date());
        selected.setCreater(webUserController.getLoggedUser());
        selected.setSolution(getSolution());
        ImageType imageType = ImageType.Thumbnail;
        selected.setImageType(imageType);
        return "/product/upload";
    }

    private UploadFacade getFacade() {
        return ejbFacade;
    }

    public String saveAndUploadProductImage() {
        saveAndUpload();
        Long tpid = getSelected().getId();
        selected = null;
        productImages = null;
        getProductImages();
        toUploadsNewProductImage();
        return "";
    }

    public String saveAndUploadSiteImage() {
        saveAndUpload();
        return toListUploads();
    }

    public String saveAndUploadImage() {
        saveAndUploadAnyImage();
        return toListUploads();
    }

    public String saveAndUploadBlogImage() {
        System.out.println("saveAndUploadBlogImage");
        if (messageController.getSelected() == null) {
            JsfUtil.addErrorMessage("No Blog to upload image");
            return "";
        }
        if (!messageController.getSelected().getMessageType().equals(MessageType.Blog)) {
            JsfUtil.addErrorMessage("Not a Blog.");
            return "";
        }
        messageController.saveSelected();
        uploadBlogImage();
        JsfUtil.addSuccessMessage("Uploaded");
        return "";
    }

    public String saveAndUploadUserImage() {
        System.out.println("saveAndUploadBlogImage");
        if (webUserController.getSelected() == null) {
            JsfUtil.addErrorMessage("No User to upload image");
            return "";
        }
        webUserController.save();
        uploadUserImage();
        JsfUtil.addSuccessMessage("Uploaded");
        return "";
    }

    public void removeSelected() {
        if (selected == null) {
            return;
        }
        selected.setRetired(true);
        selected.setRetiredAt(new Date());
        selected.setRetirer(webUserController.getLoggedUser());
        getFacade().edit(selected);
        items = null;
    }

    public void save() {
        save(selected);
    }

    public void save(Upload upload) {
        if (upload == null) {
            return;
        }
        if (upload.getId() == null) {
            if (upload.getCreatedAt() == null) {
                upload.setCreatedAt(new Date());
            }
            if (upload.getCreater() == null) {
                upload.setCreater(webUserController.getLoggedUser());
            }
            getFacade().create(upload);
        } else {
            getFacade().edit(upload);
        }
    }

    public void saveAndUploadAnyImage() {
        if (getSelected() == null) {
            return;
        }
        if (getSelected().getId() == null) {
            getFacade().create(getSelected());
        } else {
            getFacade().edit(getSelected());
        }
        getSelected().getStrId();

        InputStream in;
        if (file == null || "".equals(file.getFileName())) {
            return;
        }
        if (file == null) {
            JsfUtil.addErrorMessage("Please select an image");
            return;
        }

        if (getSelected() == null) {
            JsfUtil.addErrorMessage("Please select an Upload");
            return;
        }
        if (getSelected().getId() == null) {
            getFacade().create(getSelected());
        } else {
            getFacade().edit(getSelected());
        }
        try {
            in = getFile().getInputstream();
            File f = new File(getSelected().getId().toString() + Math.rint(100) + "");
            FileOutputStream out = new FileOutputStream(f);

            //            OutputStream out = new FileOutputStream(new File(fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            getSelected().setRetireComments(f.getAbsolutePath());
            getSelected().setFileName(file.getFileName());
            getSelected().setFileType(file.getContentType());
            in = file.getInputstream();
            getSelected().setBaImage(IOUtils.toByteArray(in));
            if (getSelected().getId() == null) {
                getFacade().create(getSelected());
            } else {
                getFacade().edit(getSelected());
            }

            switch (selected.getImageType()) {
                case Profile:
                    if (getSelected().getSolution() != null) {
                        getSelected().getSolution().setProfileImage(selected);
                        solutionFacade.edit(getSelected().getSolution());
                    }
                    break;
                case Thumbnail:
                    if (getSelected().getSolution() != null) {
                        getSelected().getSolution().setThumbnail(selected);
                        solutionFacade.edit(getSelected().getSolution());
                    }
                    break;
                case Blog_Image:
                    if (getSelected().getMessage() != null) {
                        getSelected().getMessage().setImage(selected);
                        messageFacade.edit(getSelected().getMessage());
                    }
                    break;
                case User_Image:
                    if (getSelected().getWebUser() != null) {
                        getSelected().getWebUser().setImage(selected);
                        webUserFacade.edit(getSelected().getWebUser());
                    }
                default:
                    return;
            }

            return;
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
            return;
        }

    }

    public void saveAndUpload() {
        if (getSelected() == null) {
            return;
        }
        if (getSelected().getId() == null) {
            getFacade().create(getSelected());
        } else {
            getFacade().edit(getSelected());
        }
        getSelected().getStrId();
        if (selected.getImageType() == null) {
            return;
        }

        if (getSelected().getSolution() == null) {
            JsfUtil.addErrorMessage("Select a solution");
            return;
        }

        InputStream in;
        if (file == null || "".equals(file.getFileName())) {
            return;
        }
        if (file == null) {
            JsfUtil.addErrorMessage("Please select an image");
            return;
        }

        if (getSelected() == null) {
            JsfUtil.addErrorMessage("Please select an Upload");
            return;
        }
        if (getSelected().getId() == null) {
            getFacade().create(getSelected());
        } else {
            getFacade().edit(getSelected());
        }
        try {
            in = getFile().getInputstream();
            File f = new File(getSelected().getId().toString() + Math.rint(100) + "");
            FileOutputStream out = new FileOutputStream(f);

            //            OutputStream out = new FileOutputStream(new File(fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            getSelected().setRetireComments(f.getAbsolutePath());
            getSelected().setFileName(file.getFileName());
            getSelected().setFileType(file.getContentType());
            in = file.getInputstream();
            getSelected().setBaImage(IOUtils.toByteArray(in));
            if (getSelected().getId() == null) {
                getFacade().create(getSelected());
            } else {
                getFacade().edit(getSelected());
            }

            switch (selected.getImageType()) {
                case Profile:
                    getSelected().getSolution().setProfileImage(selected);
                    solutionFacade.edit(getSelected().getSolution());
                    break;
                case Thumbnail:
                    getSelected().getSolution().setThumbnail(selected);
                    solutionFacade.edit(getSelected().getSolution());
                    break;
                default:
                    return;
            }

            return;
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
            return;
        }

    }

    private void uploadBlogImage() {
        System.out.println("uploadBlogImage = ");
        if (getSelected() == null) {
            return;
        }
        if (getSelected().getId() == null) {
            getFacade().create(getSelected());
        } else {
            getFacade().edit(getSelected());
        }
        getSelected().getStrId();
        System.out.println("getSelected().getStrId() = " + getSelected().getStrId());

        selected.setImageType(ImageType.Blog_Image);

        InputStream in;
        if (file == null || "".equals(file.getFileName())) {
            return;
        }
        if (file == null) {
            JsfUtil.addErrorMessage("Please select an image");
            return;
        }

        if (getSelected() == null) {
            JsfUtil.addErrorMessage("Please select an Upload");
            return;
        }
        if (getSelected().getId() == null) {
            getFacade().create(getSelected());
        } else {
            getFacade().edit(getSelected());
        }

        System.out.println("2. getSelected().getId() = " + getSelected().getId());

        try {
            in = getFile().getInputstream();
            File f = new File(getSelected().getId().toString() + Math.rint(100) + "");
            FileOutputStream out = new FileOutputStream(f);

            //            OutputStream out = new FileOutputStream(new File(fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            getSelected().setRetireComments(f.getAbsolutePath());
            getSelected().setFileName(file.getFileName());
            getSelected().setFileType(file.getContentType());
            in = file.getInputstream();
            getSelected().setBaImage(IOUtils.toByteArray(in));
            if (getSelected().getId() == null) {
                getFacade().create(getSelected());
            } else {
                getFacade().edit(getSelected());
            }

            messageController.getSelected().setImage(selected);
            messageController.saveSelected();
            System.out.println("messageController.getSelected() = " + messageController.getSelected().getName());;

            selected.setMessage(messageController.getSelected());
            getFacade().edit(getSelected());

            return;
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
            return;
        }

    }

    private void uploadUserImage() {
        if (getSelected() == null) {
            return;
        }
        if (getSelected().getId() == null) {
            getFacade().create(getSelected());
        } else {
            getFacade().edit(getSelected());
        }
        getSelected().getStrId();
        selected.setImageType(ImageType.User_Image);

        InputStream in;
        if (file == null || "".equals(file.getFileName())) {
            return;
        }
        if (file == null) {
            JsfUtil.addErrorMessage("Please select an image");
            return;
        }

        if (getSelected() == null) {
            JsfUtil.addErrorMessage("Please select an Upload");
            return;
        }
        if (getSelected().getId() == null) {
            getFacade().create(getSelected());
        } else {
            getFacade().edit(getSelected());
        }
        try {
            in = getFile().getInputstream();
            File f = new File(getSelected().getId().toString() + Math.rint(100) + "");
            FileOutputStream out = new FileOutputStream(f);

            //            OutputStream out = new FileOutputStream(new File(fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            getSelected().setRetireComments(f.getAbsolutePath());
            getSelected().setFileName(file.getFileName());
            getSelected().setFileType(file.getContentType());
            in = file.getInputstream();
            getSelected().setBaImage(IOUtils.toByteArray(in));
            if (getSelected().getId() == null) {
                getFacade().create(getSelected());
            } else {
                getFacade().edit(getSelected());
            }

            webUserController.getSelected().setImage(selected);
            webUserController.save();

            selected.setWebUser(webUserController.getSelected());
            getFacade().edit(getSelected());

            return;
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
            return;
        }

    }

    public Upload prepareCreate() {
        selected = new Upload();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleClinical").getString("UploadCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleClinical").getString("UploadUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleClinical").getString("UploadDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Upload> getItems() {
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

    public Upload getUpload(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Upload> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Upload> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public UploadFacade getEjbFacade() {
        return ejbFacade;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public ItemController getItemController() {
        return itemController;
    }

    public void setItemController(ItemController itemController) {
        this.itemController = itemController;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        productImages = null;
    }

    public List<Upload> getProductImages() {
        if (productImages == null) {
            productImages = fillImages(getSolution());
        }
        return productImages;
    }

    public void setProductImages(List<Upload> productImages) {
        this.productImages = productImages;
    }

    public String toImageIndex() {
        return "/upload/index";
    }

    private List<Upload> fillImages(Solution product) {
        String j = "select u from Upload u "
                + " where u.retired<>:ret "
                + " and u.product=:p ";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("p", product);
        return getFacade().findByJpql(j, m);

    }

    @FacesConverter(forClass = Upload.class)
    public static class UploadControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UploadController controller = (UploadController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "uploadController");
            return controller.getUpload(getKey(value));
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
            if (object instanceof Upload) {
                Upload o = (Upload) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Upload.class.getName()});
                return null;
            }
        }

    }

}
