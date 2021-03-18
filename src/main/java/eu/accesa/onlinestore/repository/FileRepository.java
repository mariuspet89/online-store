package eu.accesa.onlinestore.repository;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.BsonValue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileRepository {

    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;

    public FileRepository(GridFsTemplate gridFsTemplate, GridFSBucket gridFSBucket) {
        this.gridFsTemplate = gridFsTemplate;
        this.gridFSBucket = gridFSBucket;
    }

    public ObjectId store(String filename, String contentType, Object metadata, InputStream content) {
        return gridFsTemplate.store(content, filename, contentType, metadata);
    }

    public GridFSFile findImageOfProductByImageId(String id) {
        return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
    }

    public List<GridFSFile> findImagesOfProductByProductId(String productId) {
        GridFSFindIterable files = gridFsTemplate.find(new Query(Criteria.where("metadata.owner").is(productId)));

        return files.into(new ArrayList<>());
    }

    public GridFSDownloadStream getContent(BsonValue id) {
        return gridFSBucket.openDownloadStream(id);
    }
}
