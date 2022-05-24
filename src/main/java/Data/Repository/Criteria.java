package Data.Repository;

import Data.Entity.FilePath;
import Data.Entity.Tag;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import java.util.List;

@Repository
public class Criteria {
    
    @Autowired
    private SessionFactory sf;
    
    public List<FilePath> findInTag(List<Tag> tags){
        Session session
                = sf.openSession();
        CriteriaBuilder builder
                = session.getCriteriaBuilder();
        CriteriaQuery<FilePath> q1 = builder.createQuery(FilePath.class);
        Root<FilePath> fr = q1.from(FilePath.class);
        fr.fetch("fileType");

        SetJoin<Object, Object> t = fr.joinSet("tags");
        t.on(t.get("id").in(tags));
        q1
                .groupBy(fr.get("id"))
                .having(builder.equal(builder.count(t.get("id")),tags.size()));

        Query<FilePath> q = session.createQuery(q1);
        List<FilePath> list = q.list();

        return list;
    }
    
}
