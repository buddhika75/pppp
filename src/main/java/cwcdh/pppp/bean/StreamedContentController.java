/*
 * The MIT License
 *
 * Copyright 2020 hiu_pdhs_sp.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cwcdh.pppp.bean;

import cwcdh.pppp.entity.Solution;
import cwcdh.pppp.facade.SolutionFacade;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author hiu_pdhs_sp
 */
@Named(value = "streamedContentController")
@RequestScoped
public class StreamedContentController {

    @Inject
    private SolutionFacade solutionFacade;

    /**
     * Creates a new instance of StreamedContentController
     */
    public StreamedContentController() {
    }

    public StreamedContent getSolutionIconById() {
        System.err.println("Get Solution Icon By Id");
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getRenderResponse()) {
            System.err.println("Contex Response");
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Get ID value from actual request param.
            System.out.println("So, browser is requesting the image. Get ID value from actual request param.");
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            System.err.println("Solution Id " + id);
            Long l;
            try {
                l = Long.valueOf(id);
            } catch (NumberFormatException e) {
                l = 0l;
            }
            String j = "select s from Solution s where s.id=:id";
            Map m = new HashMap();
            m.put("id", l);
            System.out.println("m = " + m);
            System.out.println("j = " + j);
            Solution temImg = getSolutionFacade().findFirstByJpql(j, m);
            System.out.println("temImg = " + temImg);
            if (temImg != null) {
                System.err.println("Img 1 " + temImg);
                byte[] imgArr = null;
                try {
                    imgArr = temImg.getBaImageThumb();
                } catch (Exception e) {
                    System.err.println("Try  " + e.getMessage());
                    return new DefaultStreamedContent();
                }
                if (imgArr == null) {
                    System.err.println("No thummbnail  " );
                    return new DefaultStreamedContent();
                }
                StreamedContent str = new DefaultStreamedContent(new ByteArrayInputStream(imgArr), temImg.getFileTypeIcon());
                System.err.println("Stream " + str);
                return str;
            } else {
                return new DefaultStreamedContent();
            }
        }
    }

    public SolutionFacade getSolutionFacade() {
        return solutionFacade;
    }

    public void setSolutionFacade(SolutionFacade solutionFacade) {
        this.solutionFacade = solutionFacade;
    }

}
