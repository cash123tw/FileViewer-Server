package App.Init;

import Web.Service.LocalFileWalker.Serv_localWalk_impl;
import lombok.NoArgsConstructor;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@NoArgsConstructor
public class Init implements ApplicationListener<ContextRefreshedEvent>, MetadataBuilderContributor {

    boolean reScan;

    public Init(boolean reScan) {
        this.reScan = reScan;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (reScan) {
            Serv_localWalk_impl bean
                    = event
                    .getApplicationContext()
                    .getBean(Serv_localWalk_impl.class);
            bean.walkLocalFileAndSaveToRepository();
        }
    }

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder
                .applySqlFunction(
                        "group_concat",
                        new StandardSQLFunction("group_concat", StandardBasicTypes.STRING)
                );
    }
}
