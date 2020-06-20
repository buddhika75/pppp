package cwcdh.pppp.facade;

import cwcdh.pppp.entity.EvaluationGroup;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Dr M H B Ariyaratne, buddhika.ari@gmail.com
 */
@Stateless
public class EvaluationGroupFacade extends AbstractFacade<EvaluationGroup> {

    @PersistenceContext(unitName = "hmisPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EvaluationGroupFacade() {
        super(EvaluationGroup.class);
    }

}
