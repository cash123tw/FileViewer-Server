package Web.Service.LocalFileWalker;

import Bean.FileDetail;
import Worker.FileExploreWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
public interface Serv_localServWalk {

    boolean walkLocalFile(Predicate<File> predicate);

}
