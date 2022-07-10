package Data.Criteria;

import Data.Entity.FilePath;
import Data.Entity.FileType;
import Data.Entity.Tag;
import Data.Repository.FilePathRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.graph.RootGraph;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
//@Component
public class MineCriteria {

    private SessionFactory sf;
    private FilePathRepository filePathRepository;

    @Autowired
    public MineCriteria(SessionFactory sf, FilePathRepository filePathRepository) {
        this.sf = sf;
        this.filePathRepository = filePathRepository;
    }

    public List<FilePath> findInTag(List<Tag> tags) {
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
                .having(builder.equal(builder.count(t.get("id")), tags.size()));

        Query<FilePath> q = session.createQuery(q1);
        List<FilePath> list = q.list();

        return list;
    }

    public List<FilePath> findByParam(FilePath root, String fileName, FileType fileType, Set<Tag> tags,Integer limitStart,Integer dataLength) {
        List<Integer> ids
                = filePathRepository.findAllByRootIdAndFileName(root.getId(),fileName);
        Session session
                = sf.openSession();
        CriteriaBuilder builder
                = session.getCriteriaBuilder();
        CriteriaQuery<FilePath> filePathQuery
                = builder.createQuery(FilePath.class);
        Root<FilePath> filePathRoot
                = filePathQuery.from(FilePath.class);

        filePathQuery.where(filePathRoot.get("id").in(ids));
        filePathRoot.fetch("fileType");

        if(Objects.nonNull(fileType)){
            Join<Object, Object> fileTypeJoin
                    = filePathRoot.join("fileType");
            fileTypeJoin.on(builder.equal(fileTypeJoin.get("id"),fileType.getId()));
        }

        if (Objects.nonNull(tags) && tags.size() != 0) {
            List<Integer> tag_ids
                    = tags.stream().map(Tag::getId).toList();
            SetJoin<Object, Object> tagsJoin
                    = filePathRoot.joinSet("tags");
            tagsJoin.on(tagsJoin.get("id").in(tag_ids));

            filePathQuery.groupBy(filePathRoot.get("id"))
                    .having(builder.equal(builder.count(tagsJoin.get("id")), tag_ids.size()));
        } else{
            filePathQuery.groupBy(filePathRoot.get("id"));
        }

        Query<FilePath> query = session.createQuery(filePathQuery);
        query.setFirstResult(limitStart);
        query.setMaxResults(dataLength);

        List<Integer> target_ids
                = query.list().stream().map(FilePath::getId).toList();

        return filePathRepository.findFilePathsByIdIsInIds(target_ids);
    }

}
