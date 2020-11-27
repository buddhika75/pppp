package cwcdh.pppp.bean;

import cwcdh.pppp.bean.util.JsfUtil;
import cwcdh.pppp.entity.Message;
import cwcdh.pppp.entity.Upload;
import cwcdh.pppp.enums.ImageType;
import cwcdh.pppp.enums.MessageType;
import cwcdh.pppp.facade.MessageFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class MessageController implements Serializable {

    // <editor-fold defaultstate="collapsed" desc="EJBs">
    @EJB
    private MessageFacade facade;
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Controllers">
    @Inject
    private WebUserController webUserController;
    @Inject
    private CommonController commonController;
    @Inject
    private UploadController uploadController;
    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Class Variables">

    private List<Message> items = null;
    private Message selected;
    private Message previousBlog;
    private Message nextBlog;
    private Message subscribing;
    private List<Long> blogIds;
    private List<Message> pageBlogs = null;
    private Message displayedBlog;
    int blogPageNumber;
    private int maxPageNumber;
    private Integer visiblePg1;
    private Integer visiblePg2;
    private Integer visiblePg3;
    private Integer visiblePg4;
    private Integer visiblePg5;
    private boolean visiblePg1Focus;
    private boolean visiblePg2Focus;
    private boolean visiblePg3Focus;
    private boolean visiblePg4Focus;
    private boolean visiblePg5Focus;
    private List<Message> mostPopular = null;

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Navigator Methods">
    public void nextBlogPage() {
        System.out.println("nextBlogPage");
        System.out.println("blogPageNumber" + blogPageNumber);
        System.out.println("maxPageNumber" + maxPageNumber);
        if (blogPageNumber < maxPageNumber) {
            blogPageNumber++;
        }
        pageBlogs = fillPageBlocks();
        calculateVisiblePageNumbers();
    }

    public void previousBlogPage() {
        System.out.println("previousBlogPage");
        System.out.println("blogPageNumber = " + blogPageNumber);
        if (blogPageNumber <= 2) {
            blogPageNumber--;
        }
        pageBlogs = fillPageBlocks();
        calculateVisiblePageNumbers();
    }

    public void selectPage1() {
        toSelectedBlogPage(1);
    }

    public void selectPage2() {
        toSelectedBlogPage(2);
    }

    public void selectPage3() {
        toSelectedBlogPage(3);
    }

    public void selectPage4() {
        toSelectedBlogPage(4);
    }

    public void selectPage5() {
        toSelectedBlogPage(5);
    }

    public void toSelectedBlogPage(int pagePosition) {
        switch (pagePosition) {
            case 1:
                blogPageNumber = visiblePg1;
                break;
            case 2:
                blogPageNumber = visiblePg2;
                break;

            case 3:
                blogPageNumber = visiblePg3;
                break;

            case 4:
                blogPageNumber = visiblePg4;
                break;

            case 5:
                blogPageNumber = visiblePg5;
                break;
        }
        pageBlogs = fillPageBlocks();
    }

    private void calculateVisiblePageNumbers() {
        if (maxPageNumber < 6) {
            visiblePg1 = 1;
            visiblePg2 = 2;
            visiblePg3 = 3;
            visiblePg4 = 4;
            visiblePg5 = 5;
        } else if ((blogPageNumber + 6) > maxPageNumber) {
            visiblePg1 = maxPageNumber - 4;
            visiblePg2 = maxPageNumber - 3;
            visiblePg3 = maxPageNumber - 2;
            visiblePg4 = maxPageNumber - 1;
            visiblePg5 = maxPageNumber;
        } else {
            visiblePg1++;
            visiblePg2++;
            visiblePg3++;
            visiblePg4++;
            visiblePg5++;
        }
        visiblePg1Focus = false;
        visiblePg2Focus = false;
        visiblePg3Focus = false;
        visiblePg4Focus = false;
        visiblePg5Focus = false;
        if (blogPageNumber == visiblePg1) {
            visiblePg1Focus = true;
        } else if (blogPageNumber == visiblePg2) {
            visiblePg2Focus = true;
        } else if (blogPageNumber == visiblePg3) {
            visiblePg3Focus = true;
        } else if (blogPageNumber == visiblePg4) {
            visiblePg4Focus = true;
        } else if (blogPageNumber == visiblePg5) {
            visiblePg5Focus = true;
        }
    }

    public String toSubmitProject() {
        selected = new Message();
        selected.setMessageType(MessageType.Project_Submission);
        return "/upload";
    }

    public String toContact() {
        selected = new Message();
        selected.setMessageType(MessageType.Contact_us);
        return "/contact";
    }
    
    public String toSubmitSolution() {
        selected = new Message();
        selected.setMessageType(MessageType.Project_Submission);
        return "/submit";
    }

    public String toBlog() {
        blogPageNumber = 1;
        blogIds = fillBlockIds();
        pageBlogs = fillPageBlocks();
        maxPageNumber = ((blogIds.size() - 1) / 5) + 1;
        mostPopular = fillPopular();
        calculateVisiblePageNumbers();
        return "/blog";
    }

    public String toCreateNewBlog() {
        selected = new Message();
        selected.setMessageType(MessageType.Blog);

        Upload upload = new Upload();
        upload.setCreatedAt(new Date());
        upload.setCreater(webUserController.getLoggedUser());
        ImageType imageType = ImageType.Blog_Image;
        upload.setImageType(imageType);

        selected.setImage(upload);
        uploadController.setSelected(upload);
        return "/messages/blog";
    }

    public String toViewContactUs() {
        if (selected == null) {
            return "";
        }
        return "/messages/contact_us";
    }
    
    public String toViewContactSubscreption() {
        if (selected == null) {
            return "";
        }
        return "/messages/contact_subscreption";
    }
    
     public String toViewContactSubmission() {
        if (selected == null) {
            return "";
        }
        return "/messages/contact_submission";
    }

    public String deleteBlog() {
        if (selected == null) {
            return "";
        }
        if (!selected.getMessageType().equals(MessageType.Blog)) {
            return toViewBlogs();
        }
        selected.setRetired(true);
        selected.setRetiredAt(new Date());
        selected.setRetiredBy(webUserController.getLoggedUser());
        getFacade().edit(selected);
        items = null;
        blogIds = null;
        JsfUtil.addSuccessMessage("Deleted");
        return toViewBlogs();
    }

    public String toViewBlog() {
        if (selected == null) {
            return "";
        }
        if (selected.getImage() == null) {
            Upload upload = new Upload();
            upload.setCreatedAt(new Date());
            upload.setCreater(webUserController.getLoggedUser());
            ImageType imageType = ImageType.Blog_Image;
            upload.setImageType(imageType);
            selected.setImage(upload);
        }
        uploadController.setSelected(selected.getImage());
        return "/messages/blog";
    }

    public String toViewBlogPublic() {
        if (selected == null) {
            return "";
        }
        previousBlog = findPreviousBlog(selected);
        nextBlog = findNextBlog(selected);
        selected.setViewCount(selected.getViewCount() + 1);
        getFacade().edit(selected);
        return "/blog_detail";
    }

    public Message findPreviousBlog(Message b) {
        if (b == null) {
            return null;
        }
        String jpql = "Select m "
                + " from Message m "
                + " where m.retired<>:ret "
                + " and m.messageType=:mt "
                + " and m.id<:id "
                + "order by m.id desc";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("mt", MessageType.Blog);
        m.put("id", b.getId());
        return getFacade().findFirstByJpql(jpql, m);
    }

    public Message findNextBlog(Message b) {
        if (b == null) {
            return null;
        }
        String jpql = "Select m "
                + " from Message m "
                + " where m.retired<>:ret "
                + " and m.messageType=:mt "
                + " and m.id>:id "
                + "order by m.id";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("mt", MessageType.Blog);
        m.put("id", b.getId());
        return getFacade().findFirstByJpql(jpql, m);
    }

    public String toViewCaseStudyForUsers() {
        if (selected == null) {
            return "";
        }
        return "/messages/casestudy";
    }

    public String toViewCaseStudyForPublic() {
        if (selected == null) {
            return "";
        }
        return "/casestudy";
    }

    public String toViewContactUss() {
        items = listMessages(MessageType.Contact_us);
        return "/messages/contact_uss";
    }

    public String toSubscreptions() {
        items = listMessages(MessageType.Email_Subscreption);
        return "/messages/contact_subscriptions";
    }

    public String toSubmissions() {
        items = listMessages(MessageType.Project_Submission);
        return "/messages/contact_submissions";
    }

    public String toViewBlogs() {
        items = listMessages(MessageType.Blog);
        return "/messages/blogs";
    }

    public String toViewCaseStudiesForUsers() {
        items = listMessages(MessageType.Cas_Study);
        return "/messages/casestudies";
    }

    public String toViewCaseStudiesForPublic() {
        items = listMessages(MessageType.Cas_Study);
        return "/casestudies";
    }

    public String toCreateNewCaseStudy() {
        selected = new Message();
        selected.setMessageType(MessageType.Cas_Study);
        selected.setCreatedAt(new Date());
        selected.setCreatedBy(webUserController.getLoggedUser());
        return "/messages/casestudy";
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Methods">
    public void removeImage() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        selected.setImage(null);
        saveSelected();
        JsfUtil.addErrorMessage("Removed");
    }

    public void saveSelected() {
        if (selected == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        if (selected.getImage() != null) {
            Upload u = selected.getImage();
            uploadController.save(u);
        }
        saveSelected(selected);
    }

    public String toUploadBlogImage() {
        saveSelected(selected);
        return "/messages/blog_image";
    }

    public void saveSelected(Message msg) {
        if (msg == null) {
            JsfUtil.addErrorMessage("Nothing selected to save.");
            return;
        }
        if (msg.getId() == null) {
            msg.setCreatedAt(new Date());
            msg.setCreatedBy(webUserController.getLoggedUser());
            getFacade().create(msg);
        } else {
            msg.setLastEditBy(webUserController.getLoggedUser());
            msg.setCreatedAt(new Date());
            getFacade().edit(msg);
        }
    }

    public String uploadProject() {
        saveSelected();
        JsfUtil.addSuccessMessage("Project Submitted.");
        return "/uploaded";
    }

    public String submitContact() {
        saveSelected();
        JsfUtil.addSuccessMessage("Submitted.");
        return "/contacted";
    }

    public String submitSubscribed() {
       getSubscribing();
       subscribing.setCreatedAt(new Date());
        saveSelected(subscribing);
        JsfUtil.addSuccessMessage("Submitted.");
        return "/subscribed";
    }
    
    public String submitSolution() {
        saveSelected(selected);
        JsfUtil.addSuccessMessage("Submitted.");
        return "/submitted";
    }

    public List<Message> listMessages(MessageType type) {
        String j = "select m "
                + " from Message m "
                + " where m.retired <>:ret "
                + " and m.messageType=:type"
                + " order by m.id desc";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("type", type);
        return getFacade().findByJpql(j, m);
    }

    public List<Message> completeBlogs(String qry) {
        MessageType type = MessageType.Blog;
        String j = "select m "
                + " from Message m "
                + " where m.retired <>:ret "
                + " and lower(m.name) like :qry "
                + " and m.messageType=:type"
                + " order by m.name";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("type", type);
        m.put("qry", "%" + qry.trim().toLowerCase() + "%");
        return getFacade().findByJpql(j, m);
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public MessageFacade getFacade() {
        return facade;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public List<Message> getItems() {
        return items;
    }

    public void setItems(List<Message> items) {
        this.items = items;
    }

    public Message getSelected() {
        return selected;
    }

    public void setSelected(Message selected) {
        this.selected = selected;
    }

    public Message getSubscribing() {
        if(subscribing==null){
            subscribing = new Message();
            subscribing.setMessageType(MessageType.Email_Subscreption);
        }
        return subscribing;
    }

    public void setSubscribing(Message subscribing) {
        if (subscribing == null) {
            subscribing = new Message();
            subscribing.setMessageType(MessageType.Email_Subscreption);
        }
        this.subscribing = subscribing;
    }

    public WebUserController getWebUserController() {
        return webUserController;
    }

    public List<Long> getBlogIds() {
        if (blogIds == null) {
            blogIds = fillBlockIds();
        }
        return blogIds;
    }

    public void setBlogIds(List<Long> blogIds) {
        this.blogIds = blogIds;
    }

    public List<Message> getPageBlogs() {
        return pageBlogs;
    }

    public void setPageBlogs(List<Message> pageBlogs) {
        this.pageBlogs = pageBlogs;
    }

    public Message getDisplayedBlog() {
        return displayedBlog;
    }

    public void setDisplayedBlog(Message displayedBlog) {
        this.displayedBlog = displayedBlog;
    }

    private List<Long> fillBlockIds() {
        String j = "select m.id "
                + " from Message m "
                + " where m.retired<>:ret "
                + " and m.messageType=:t "
                + " order by m.id desc";
        Map m = new HashMap();
        m.put("ret", true);
        m.put("t", MessageType.Blog);
        return getFacade().findLongList(j, m);
    }

    public int getBlogPageNumber() {
        return blogPageNumber;
    }

    public void setBlogPageNumber(int blogPage) {
        if (blogPage < 1) {
            blogPage = 1;
        }
        if (blogIds == null) {
            getBlogIds();
        }
        maxPageNumber = ((blogIds.size() - 1) / 5) + 1;
        if (blogPage > maxPageNumber) {
            blogPage = maxPageNumber;
        }
        pageBlogs = fillPageBlocks();
        this.blogPageNumber = blogPage;
    }

    private List<Message> fillPopular() {
        String j = "select m "
                + " from Message m "
                + " where m.retired=false "
                + " order by m.viewCount desc";
        Map m = new HashMap();
        return getFacade().findByJpql(j, m, 5);
    }

    private List<Message> fillPageBlocks() {
        System.out.println("fillPageBlocks");
        int startCount;
        int endCount;
        startCount = blogPageNumber * 5 - 4;
        endCount = blogPageNumber * 5;
        if (blogIds == null) {
            return null;
        }
        int blogsSize = getBlogIds().size();
        System.out.println("blogsSize = " + blogsSize);
        if (endCount > blogsSize) {
            endCount = blogsSize;
        }
        List<Long> ids = new ArrayList<>();
        System.out.println("startCount = " + startCount);
        System.out.println("endCount = " + endCount);
        for (int index = startCount; index < endCount + 1; index++) {
            Long id = getBlogIds().get(index - 1);
            System.out.println(index + ". id = " + id);
            ids.add(id);
        }
        System.out.println("1. Ids = " + ids);
        String j = "select m "
                + " from Message m "
                + " where m.id in :ids "
                + " and m.retired=false "
                + " order by m.id desc";
        Map m = new HashMap();
        m.put("ids", ids);
        return getFacade().findByJpql(j, m);
    }

    public Message getPreviousBlog() {
        return previousBlog;
    }

    public void setPreviousBlog(Message previousBlog) {
        this.previousBlog = previousBlog;
    }

    public Message getNextBlog() {
        return nextBlog;
    }

    public void setNextBlog(Message nextBlog) {
        this.nextBlog = nextBlog;
    }

    public UploadController getUploadController() {
        return uploadController;
    }

    public void setUploadController(UploadController uploadController) {
        this.uploadController = uploadController;
    }

    public int getMaxPageNumber() {
        return maxPageNumber;
    }

    public void setMaxPageNumber(int maxPageNumber) {
        this.maxPageNumber = maxPageNumber;
    }

    public Integer getVisiblePg1() {
        return visiblePg1;
    }

    public void setVisiblePg1(Integer visiblePg1) {
        this.visiblePg1 = visiblePg1;
    }

    public Integer getVisiblePg2() {
        return visiblePg2;
    }

    public void setVisiblePg2(Integer visiblePg2) {
        this.visiblePg2 = visiblePg2;
    }

    public Integer getVisiblePg3() {
        return visiblePg3;
    }

    public void setVisiblePg3(Integer visiblePg3) {
        this.visiblePg3 = visiblePg3;
    }

    public Integer getVisiblePg4() {
        return visiblePg4;
    }

    public void setVisiblePg4(Integer visiblePg4) {
        this.visiblePg4 = visiblePg4;
    }

    public Integer getVisiblePg5() {
        return visiblePg5;
    }

    public void setVisiblePg5(Integer visiblePg5) {
        this.visiblePg5 = visiblePg5;
    }

    public boolean isVisiblePg1Focus() {
        return visiblePg1Focus;
    }

    public void setVisiblePg1Focus(boolean visiblePg1Focus) {
        this.visiblePg1Focus = visiblePg1Focus;
    }

    public boolean isVisiblePg2Focus() {
        return visiblePg2Focus;
    }

    public void setVisiblePg2Focus(boolean visiblePg2Focus) {
        this.visiblePg2Focus = visiblePg2Focus;
    }

    public boolean isVisiblePg3Focus() {
        return visiblePg3Focus;
    }

    public void setVisiblePg3Focus(boolean visiblePg3Focus) {
        this.visiblePg3Focus = visiblePg3Focus;
    }

    public boolean isVisiblePg4Focus() {
        return visiblePg4Focus;
    }

    public void setVisiblePg4Focus(boolean visiblePg4Focus) {
        this.visiblePg4Focus = visiblePg4Focus;
    }

    public boolean isVisiblePg5Focus() {
        return visiblePg5Focus;
    }

    public void setVisiblePg5Focus(boolean visiblePg5Focus) {
        this.visiblePg5Focus = visiblePg5Focus;
    }

    public List<Message> getMostPopular() {
        if (mostPopular == null) {
            mostPopular = fillPopular();
        }
        return mostPopular;
    }

    public void setMostPopular(List<Message> mostPopular) {
        this.mostPopular = mostPopular;
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Converters">
    @FacesConverter(forClass = Message.class)
    public static class MessageControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MessageController controller = (MessageController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "messageController");
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
            if (object instanceof Message) {
                Message o = (Message) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Message.class.getName()});
                return null;
            }
        }

    }

    @FacesConverter(value = "messageConverter")
    public static class MessageConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MessageController controller = (MessageController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "messageController");
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
            if (object instanceof Message) {
                Message o = (Message) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Message.class.getName()});
                return null;
            }
        }

    }

    // </editor-fold>
}
